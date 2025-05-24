package com.kkumteul.config.job;

import static com.kkumteul.util.redis.RedisKey.BOOK_LIKE_EVENT_LIST;

import com.kkumteul.config.job.listener.MetricsJobListener;
import com.kkumteul.config.job.processor.CachingScoreUpdateProcessor;
import com.kkumteul.config.job.writer.ScoreUpdateEventWriter;
import com.kkumteul.domain.book.service.BookService;
import com.kkumteul.domain.childprofile.service.ChildProfileService;
import com.kkumteul.domain.childprofile.service.ChildProfileUpdateService;
import com.kkumteul.domain.childprofile.service.PersonalityScoreService;
import com.kkumteul.domain.history.service.ChildPersonalityHistoryService;
import com.kkumteul.domain.mbti.service.MBTIService;
import com.kkumteul.dto.ScoreUpdateEventDto;
import com.kkumteul.util.redis.RedisUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ChangePersonalityBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RedisUtil redisUtil;
    private final ChildProfileService childProfileService;
    private final PersonalityScoreService personalityScoreService;
    private final ChildPersonalityHistoryService historyService;
    private final BookService bookService;
    private final MBTIService mbtiService;
    private final ChildProfileUpdateService childProfileUpdateService;

    @Bean
    public Job processLikeDislikeEventsJob() {
        return new JobBuilder("processLikeDislikeEventsJob", jobRepository)
                .start(processLikeDislikeEventsStep())
                .listener(new MetricsJobListener())
                .build();
    }

    @Bean
    public Step processLikeDislikeEventsStep() {
        return new StepBuilder("processLikeDislikeEventsStep", jobRepository)
                .<String, ScoreUpdateEventDto>chunk(750, transactionManager)
                .reader(redisEventReader())
                .processor(cachingScoreUpdateProcessor())
                .writer(scoreUpdateEventWriter())
                .faultTolerant()
                    .retryLimit(3)
                    .retry(Exception.class)
                .listener(chunkListener())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<String> redisEventReader() {

        List<Object> allEvents = redisUtil.getAllFromList(BOOK_LIKE_EVENT_LIST.getKey());
        List<String> events = allEvents.stream()
                .map(Object::toString)
                .toList();

        return new ListItemReader<>(events);
    }

    @Bean
    @StepScope
    public CachingScoreUpdateProcessor cachingScoreUpdateProcessor() {
        return new CachingScoreUpdateProcessor(childProfileService, bookService);
    }

    @Bean
    public ItemWriter<ScoreUpdateEventDto> scoreUpdateEventWriter() {
        return new ScoreUpdateEventWriter(personalityScoreService, childProfileService, historyService, mbtiService, childProfileUpdateService);
    }

    @Bean
    public ChunkListener chunkListener() {
        return new ChunkListener() {
            @Override
            public void beforeChunk(ChunkContext context) {
                log.info("Chunk 처리 시작");
            }

            @Override
            public void afterChunk(ChunkContext context) {
                cachingScoreUpdateProcessor().clearCache();
                log.info("Chunk 처리 완료");
            }

            @Override
            public void afterChunkError(ChunkContext context) {
                cachingScoreUpdateProcessor().clearCache();
                Throwable throwable = (Throwable) context.getAttribute(ChunkListener.ROLLBACK_EXCEPTION_KEY);
                log.error("Chunk 처리 실패: {}", throwable.getMessage(), throwable);
            }
        };
    }
}
