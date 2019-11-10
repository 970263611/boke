package com.dahua.boke.aspect;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DwqAutoConfiguration {

    @Bean
    public DwqAnnotationAspect dwqAnnotationAspect(){
        return new DwqAnnotationAspect();
    }
}
