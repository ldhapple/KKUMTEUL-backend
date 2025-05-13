package com.kkumteul.config.job.listener;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;

@Slf4j
public class MetricsJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        LocalDateTime start = jobExecution.getStartTime();
        LocalDateTime end = jobExecution.getEndTime();
        long jobDuration = Duration.between(start, end).toMillis();
        log.info("Job '{}' completed in {} ms",
                jobExecution.getJobInstance().getJobName(), jobDuration);

        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            LocalDateTime stepStart = stepExecution.getStartTime();
            LocalDateTime stepEnd = stepExecution.getEndTime();
            long stepDuration = Duration.between(stepStart, stepEnd).toMillis();
            log.info("Step '{}' Metrics:", stepExecution.getStepName());
            log.info("  Read Count      : {}", stepExecution.getReadCount());
            log.info("  Write Count     : {}", stepExecution.getWriteCount());
            log.info("  Filter Count    : {}", stepExecution.getFilterCount());
            log.info("  Skip Count      : {} (process: {}, write: {})",
                    stepExecution.getSkipCount(),
                    stepExecution.getProcessSkipCount(),
                    stepExecution.getWriteSkipCount());
            log.info("  소요 시간        : {} ms", stepDuration);
        }
    }
}
