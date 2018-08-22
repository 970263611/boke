
package com.text;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling 
public class TestApplication{

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}
	
}
