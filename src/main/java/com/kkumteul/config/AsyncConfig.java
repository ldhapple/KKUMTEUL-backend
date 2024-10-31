package com.kkumteul.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync  // 비동기 기능 활성화
public class AsyncConfig {
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);  // 최소 스레드 수
        executor.setMaxPoolSize(4);   // 최대 스레드 수
        executor.setQueueCapacity(10);  // 대기 큐 크기
        executor.setThreadNamePrefix("AsyncExecutor-");  // 스레드 이름 접두사
        executor.initialize();
        System.out.println("AsyncExecutor initialized with pool size: " + executor.getCorePoolSize());

        return executor;
    }
}
