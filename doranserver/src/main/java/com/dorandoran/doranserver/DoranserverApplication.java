package com.dorandoran.doranserver;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DoranserverApplication {

//	@Autowired
//	FirebaseConfig firebaseConfig;

	public static void main(String[] args) {
		SpringApplication.run(DoranserverApplication.class, args);
	}

}
