package com.kkumteul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.kkumteul.domain.user.repository") // UserRepository 경로 지정
public class KkumteulApplication {
	public static void main(String[] args) {
		SpringApplication.run(KkumteulApplication.class, args);
	}
}