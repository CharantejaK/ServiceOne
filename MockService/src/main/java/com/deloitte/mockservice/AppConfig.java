package com.deloitte.mockservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication	
@EnableAutoConfiguration(exclude = {
org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class
})
public class AppConfig {
	

	public static void main(String[] args) {
		SpringApplication.run(AppConfig.class, args);
	}
}
