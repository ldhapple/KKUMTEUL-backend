package com.kkumteul.config.scheduler;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@EnableScheduling
@Configuration
@RequiredArgsConstructor
public class DeleteHistoryScheduler {

    private final JobLauncher jobLauncher;
    private final Job deleteHistoryJob;

    @Scheduled(cron = "0 0 4 * * ?") // 매일 새벽 4시에 실행
    public void runDeleteHistoryJob() {
        log.info("delete history job time: {}", LocalDateTime.now());

        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("DeleteHistoryJob", String.valueOf(System.currentTimeMillis()))
                    .toJobParameters();
            jobLauncher.run(deleteHistoryJob, params);
        } catch (Exception e) {
            log.error("Error", e);
        }
    }
}
