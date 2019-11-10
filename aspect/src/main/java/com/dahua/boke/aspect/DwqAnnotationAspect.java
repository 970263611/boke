package com.dahua.boke.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Aspect
@Component
public class DwqAnnotationAspect {

    public static Logger logger = LoggerFactory.getLogger(DwqAnnotationAspect.class);

    @Pointcut("within(@com.dahua.boke.aspect.DwqAnnotation *)")
    public void annotationPoint() {};

    @Around("annotationPoint()")
    public Object around(ProceedingJoinPoint joinPoint){
        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        String classType = joinPoint.getTarget().getClass().getName();
        Class<?> clazz = null;
        try {
            clazz = Class.forName(classType);
        } catch (ClassNotFoundException e) {
            logger.info("----------获取当前方法异常----------");
        }
        String clazzName = clazz.getName();
        String methodName = joinPoint.getSignature().getName();
        logger.info("进入方法开始----------"+"clazzName: "+clazzName+", methodName:"+methodName+"时间："+dateFormat.format(date));
        try {
            Object[] args = joinPoint.getArgs();
            return joinPoint.proceed(args);
        } catch (Throwable t) {
            logger.info("执行方法异常----------"+"clazzName: "+clazzName+", methodName:"+methodName+"时间："+dateFormat.format(date));
            throw new RuntimeException(t);
        } finally {
            logger.info("执行方法结束----------"+"clazzName: "+clazzName+", methodName:"+methodName+"时间："+dateFormat.format(date));
        }
    }
}
