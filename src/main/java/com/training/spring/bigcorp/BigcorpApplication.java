package com.training.spring.bigcorp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BigcorpApplication {
	public static void main(String[] args) {
		SpringApplication.run(BigcorpApplication.class, args);
	}
}