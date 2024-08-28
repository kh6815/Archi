package com.architecture.archi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ArchiApplication {
	//TODO gitgnore 작성해야됨
	public static void main(String[] args) {
		SpringApplication.run(ArchiApplication.class, args);
	}

}
