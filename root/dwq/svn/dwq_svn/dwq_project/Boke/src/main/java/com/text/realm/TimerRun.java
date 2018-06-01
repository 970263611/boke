package com.text.realm;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TimerRun{
	
	 private static Logger logger = LoggerFactory.getLogger(TimerRun.class);
     
	    public static void main(String[] args) {  
	    	SpringApplication.run(TimerRun.class, args);  
	        logger.info(new Date()+"定时器开始启动");  
	    } 
}
