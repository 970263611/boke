
package com.boke;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;

@SpringBootApplication
@EnableDubboConfiguration
@CrossOrigin//允许跨越访问
@ImportResource({"classpath:dubbo-provider.xml"}) //加入spring的bean的xml文件
public class ProviderApplication{

	public static void main(String[] args) {
		SpringApplication.run(ProviderApplication.class, args);
	}
	
}
