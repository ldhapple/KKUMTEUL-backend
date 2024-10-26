package com.kkumteul.config;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.repository.BookRepository;
import com.kkumteul.domain.recommendation.dto.BookDataDto;
import com.kkumteul.domain.recommendation.dto.ChildDataDto;
import com.kkumteul.domain.recommendation.dto.RecommendationResultDto;
import com.kkumteul.domain.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RecommendationService recommendationService;
    private final BookRepository bookRepository;

    @Bean
    public Job recommendationJob() {
        log.info("=============job 시작=============");
        return new JobBuilder("recommendationJob", jobRepository)
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.info("===== Job 시작함 =====");
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.info("===== Job 종료. 상태: {} =====", jobExecution.getStatus());
                    }
                })
                .incrementer(new RunIdIncrementer()) // 매 실행마다 새로운 ID 생성
                .preventRestart() // 이전 인스턴스가 완료되어도 무시하고 실행
                .start(recommendationStep())
                .build();
    }
//    @JobScope
    @Bean
    @JobScope
    public Step recommendationStep() {
        log.info("=============step 시작=============");
        return new StepBuilder("recommendationStep", jobRepository)
                .<Long, RecommendationResultDto>chunk(50, transactionManager)
                .reader(activeUserReader())
                .processor(recommendationProcessor())
                .writer(recommendationWriter())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<Long> activeUserReader() {
        log.info("=============Reader 시작=============");
        List<Long> activeUserIds = recommendationService.getActiveUserIds();
        log.info("Fetched active user IDs: {}", activeUserIds);
        return new IteratorItemReader<>(activeUserIds);
    }

    @Bean
    @StepScope
    public ItemProcessor<Long, RecommendationResultDto> recommendationProcessor() {
        log.info("=============Processor 시작=============");
        return userId -> {
            log.info("Processing userId: {}", userId);

            List<BookDataDto> allBooks = recommendationService.getAllBookInfo(
                    bookRepository.findAllBooksWithTopicsAndGenre()
            );
            List<ChildDataDto> childProfiles = recommendationService.getChildrenInfo();

            List<Book> recommendedBooks = recommendationService.getRecommendations(userId, allBooks, childProfiles);
            log.info("Recommended books for user {}: {}", userId, recommendedBooks.size());

            return new RecommendationResultDto(userId, recommendedBooks);
        };
    }

    @Bean
    @StepScope
    public ItemWriter<RecommendationResultDto> recommendationWriter() {
        log.info("=============Writer 시작==============");
        return results -> {
            for (RecommendationResultDto result : results) {
                log.info("Saving recommendations for user {}: {}", result.getUserId(), result.getBooks().size());
                recommendationService.saveRecommendations(result.getUserId(), result.getBooks());
            }
        };
    }
}
