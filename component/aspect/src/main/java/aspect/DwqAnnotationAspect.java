package aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Aspect
@Component
public class DwqAnnotationAspect {

    public static Logger logger = LoggerFactory.getLogger(DwqAnnotationAspect.class);

    @Pointcut("within(@aspect.DwqAnnotation *)")
    public void annotationPoint() {
    }

    @Around("annotationPoint()")
    public Object around(ProceedingJoinPoint joinPoint) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd :hh:mm:ss");
        String classType = joinPoint.getTarget().getClass().getName();
        Class<?> clazz = null;
        try {
            clazz = Class.forName(classType);
        } catch (ClassNotFoundException e) {
            logger.error("----------获取当前方法异常----------" + "时间：" + LocalDateTime.now().format(dtf));
        }
        String clazzName = clazz.getName();
        String methodName = joinPoint.getSignature().getName();
        try {
            Object[] args = joinPoint.getArgs();
            return joinPoint.proceed(args);
        } catch (Throwable t) {
            logger.error("执行方法异常----------" + "clazzName: " + clazzName + ", methodName:" + methodName + "时间：" + LocalDateTime.now().format(dtf));
            throw new RuntimeException(t);
        }
    }
}
