package com.kkumteul.domain.recommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;

import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class BatchScheduler {

    private final Job recommendationJob;
    private final JobLauncher jobLauncher;

    // 1분마다 배치 작업 실행
//    @Scheduled(cron = "0 */1 * * * ?")

    // 추천 도서 배치 작업 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * ?")
    public synchronized void runRecommendationJob() {
        try {
//            // 현재 시간이 자정인지 확인
//            LocalDateTime now = LocalDateTime.now();
//            if (now.getHour() != 0) {
//                // 자정이 아니면 작업을 건너뜀
//                log.info("현재 자정이 아니므로 스케줄러 작업을 건너뜁니다.");
//                return;
//            }

//            log.info("===== 배치 스케줄러 시작 =====");

            JobParameters params = new JobParametersBuilder()
                    .addString("jobId", UUID.randomUUID().toString())
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(recommendationJob, params);
            log.info("Job 상태: {}", jobExecution.getStatus());
        } catch (Exception e) {
            log.error("배치 실행 중 오류 발생", e);
        }
    }

}
