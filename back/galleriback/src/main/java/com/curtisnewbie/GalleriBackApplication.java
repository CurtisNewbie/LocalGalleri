package com.curtisnewbie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GalleriBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(GalleriBackApplication.class, args);
	}

}
