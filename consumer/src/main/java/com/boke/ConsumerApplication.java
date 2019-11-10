
package com.boke;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
@EnableDubboConfiguration
@ImportResource({"classpath:dubbo-consumer.xml"}) //加入spring的bean的xml文件
public class ConsumerApplication{

	public static void main(String[] args) {
		//解决dubbo qos同一服务器启动端口占用问题
		System.setProperty("dubbo.qos.port", "33333");
		SpringApplication.run(ConsumerApplication.class, args);
	}
	
}
