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
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ChangePersonalityScheduler {

    private final JobLauncher jobLauncher;
    private final Job processLikeDislikeEventsJob;

    @Scheduled(cron = "0 0 3 * * ?")
    public void runChangePersonalityJob() {
        log.info("changePersonalityJob time: {}", LocalDateTime.now());

        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("ChangePersonalityJob", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(processLikeDislikeEventsJob, params);
        } catch (Exception e) {
            log.error("Error", e);
        }
    }
}
