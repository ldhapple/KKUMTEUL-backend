package com.kkumteul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KkumteulApplication {

	public static void main(String[] args) {
		SpringApplication.run(KkumteulApplication.class, args);
	}


}
