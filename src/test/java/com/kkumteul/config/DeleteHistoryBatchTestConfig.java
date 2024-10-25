package com.kkumteul.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
@TestConfiguration
public class DeleteHistoryBatchTestConfig {

    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils(Job deleteHistoryJob) {
        JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
        jobLauncherTestUtils.setJob(deleteHistoryJob);
        return jobLauncherTestUtils;
    }
}
