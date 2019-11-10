package com.dahua.boke.realm;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
* 通过该类即可在普通工具类里获取spring管理的bean 
* 
*/  
@Component
public class SpringTool implements ApplicationContextAware {  
     private static ApplicationContext applicationContext;  
     
        @Override  
        public void setApplicationContext(ApplicationContext context)  
            throws BeansException {  
            SpringTool.applicationContext = context;  
        }  
        public static Object getBean(String name){  
            return applicationContext.getBean(name);  
        }   
       
}