package com.dorandoran.doranserver;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DoranserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoranserverApplication.class, args);
	}

}
