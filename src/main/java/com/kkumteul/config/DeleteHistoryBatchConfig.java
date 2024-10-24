package com.kkumteul.config;

import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.history.repository.ChildPersonalityHistoryRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DeleteHistoryBatchConfig {

    private final ChildPersonalityHistoryRepository historyRepository;

    @Bean
    public Job deleteHistoryJob(JobRepository jobRepository, Step deleteHistoryStep) {
        return new JobBuilder("deleteHistoryJob", jobRepository)
                .start(deleteHistoryStep)
                .build();
    }

    @Bean
    public Step deleteHistoryStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("deleteHistoryStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("run deleteHistory tasklet");
                    LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
                    List<ChildPersonalityHistory> historiesToDelete = historyRepository.findAllByRealDelete(oneMonthAgo);
                    historyRepository.deleteAll(historiesToDelete);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
