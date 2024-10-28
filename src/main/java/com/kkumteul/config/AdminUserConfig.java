package com.kkumteul.config;

import com.kkumteul.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminUserConfig {

    @Bean
    CommandLineRunner createAdminUser(UserService userService) {
        return args -> {
            // 관리자 계정 생성 로직
            userService.createAdminUser("ehgus", "rmawjd", "gusgml", "010-1111-2222");
        };
    }
}