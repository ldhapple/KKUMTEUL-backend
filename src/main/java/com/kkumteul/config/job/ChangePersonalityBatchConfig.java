package com.kkumteul.config.job;

import static com.kkumteul.util.redis.RedisKey.BOOK_LIKE_EVENT_LIST;

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

    /** ========================== TASK EXECUTOR ============================= */
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);  // 병렬 스레드 개수
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("partition-thread-");
        executor.initialize();
        return executor;
    }

    /** ========================== JOB CONFIGURATION ============================= */
    @Bean
    public Job processLikeDislikeEventsJob() {
        return new JobBuilder("processLikeDislikeEventsJob", jobRepository)
                .start(masterStep())
                .build();
    }

    /** ========================== MASTER STEP ============================= */
    @Bean
    public Step masterStep() {
        return new StepBuilder("masterStep", jobRepository)
                .partitioner("workerStep", redisEventPartitioner())
                .partitionHandler(partitionHandler())
                .build();
    }

    /** ========================== PARTITION HANDLER ============================= */
    @Bean
    public TaskExecutorPartitionHandler partitionHandler() {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setStep(workerStep());
        handler.setTaskExecutor(taskExecutor());
        handler.setGridSize(4);  // 파티션 수
        return handler;
    }

    /** ========================== PARTITIONER ============================= */
    @Bean
    public Partitioner redisEventPartitioner() {
        return gridSize -> {
            Map<String, ExecutionContext> partitions = new HashMap<>();
            List<Object> eventObjects = redisUtil.getAllFromList(BOOK_LIKE_EVENT_LIST.getKey());
            int partitionSize = (int) Math.ceil((double) eventObjects.size() / gridSize);

            for (int i = 0; i < gridSize; i++) {
                ExecutionContext context = new ExecutionContext();
                int startIdx = i * partitionSize;
                int endIdx = Math.min(startIdx + partitionSize, eventObjects.size());

                // 인덱스 정보만 저장 (데이터 대신)
                context.putInt("startIdx", startIdx);
                context.putInt("endIdx", endIdx);
                partitions.put("partition" + i, context);
            }
            return partitions;
        };
    }

    /** ========================== WORKER STEP ============================= */
    @Bean
    public Step workerStep() {
        return new StepBuilder("workerStep", jobRepository)
                .<String, ScoreUpdateEventDto>chunk(750, transactionManager)
                .reader(redisPartitionedEventReader(null, null))  // 파티션별 Reader
                .processor(cachingScoreUpdateProcessor())
                .writer(scoreUpdateEventWriter())
                .listener(new ChunkListener() {
                    @Override
                    public void beforeChunk(ChunkContext context) { }

                    @Override
                    public void afterChunk(ChunkContext context) {
                        cachingScoreUpdateProcessor().clearCache();
                    }

                    @Override
                    public void afterChunkError(ChunkContext context) {
                        cachingScoreUpdateProcessor().clearCache();
                    }
                })
                .build();
    }

    /** ========================== ITEM READER ============================= */
    @Bean
    @StepScope
    public ItemReader<String> redisPartitionedEventReader(
            @Value("#{stepExecutionContext['startIdx']}") Integer startIdx,
            @Value("#{stepExecutionContext['endIdx']}") Integer endIdx) {

        List<Object> allEvents = redisUtil.getAllFromList(BOOK_LIKE_EVENT_LIST.getKey());
        List<String> events = allEvents.subList(startIdx, endIdx).stream()
                .map(Object::toString)
                .toList();

        return new ListItemReader<>(events);
    }

    /** ========================== PROCESSOR ============================= */
    @Bean
    @StepScope
    public CachingScoreUpdateProcessor cachingScoreUpdateProcessor() {
        return new CachingScoreUpdateProcessor(childProfileService, bookService);
    }

    /** ========================== WRITER ============================= */
    @Bean
    public ItemWriter<ScoreUpdateEventDto> scoreUpdateEventWriter() {
        return new ScoreUpdateEventWriter(personalityScoreService, childProfileService, historyService, mbtiService, childProfileUpdateService);
    }
}
