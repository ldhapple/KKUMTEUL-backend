package com.kkumteul.config.job;

import static com.kkumteul.util.redis.RedisKey.BOOK_LIKE_EVENT_LIST;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookMBTI;
import com.kkumteul.domain.book.service.BookService;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.GenreScore;
import com.kkumteul.domain.childprofile.entity.TopicScore;
import com.kkumteul.domain.childprofile.service.ChildProfileService;
import com.kkumteul.domain.childprofile.service.PersonalityScoreService;
import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.history.entity.HistoryCreatedType;
import com.kkumteul.domain.history.entity.MBTIScore;
import com.kkumteul.domain.history.service.ChildPersonalityHistoryService;
import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.mbti.service.MBTIService;
import com.kkumteul.util.redis.RedisUtil;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

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

    @Bean
    public Job processLikeDislikeEventsJob() {
        return new JobBuilder("processLikeDislikeEventsJob", jobRepository)
                .start(processLikeDislikeEventsStep())
                .build();
    }

    @Bean
    public Step processLikeDislikeEventsStep() {
        return new StepBuilder("processLikeDislikeEventsStep", jobRepository)
                .<String, Long>chunk(100, transactionManager)
                .reader(redisEventReader())
                .processor(eventProcessor())
                .writer(eventWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<String> redisEventReader() {
        List<Object> eventObjects = redisUtil.getAllFromList(BOOK_LIKE_EVENT_LIST.getKey());
        log.info(eventObjects.toString());
        List<String> events = eventObjects.stream()
                .map(Object::toString)
                .toList();

        return new ListItemReader<>(events);
    }

    @Bean
    public ItemProcessor<String, Long> eventProcessor() {
        log.info("processor");
        return message -> {
            String[] data = message.split(":");
            Long childProfileId = Long.parseLong(data[0]);
            Long bookId = Long.parseLong(data[1]);
            String action = data[2];

            ChildProfile childProfile = childProfileService.getChildProfileWithMBTIScore(childProfileId);
            Book book = bookService.getBook(bookId);
            List<BookMBTI> bookMBTIS = book.getBookMBTIS();

            double changedScore = action.equals("LIKE") ? 2.0 : -2.0;
            personalityScoreService.updateGenreAndTopicScores(childProfile, book, changedScore);
            personalityScoreService.updateCumulativeMBTIScore(childProfileId, bookMBTIS, changedScore);

            return childProfileId;
        };
    }

    @Bean
    public ItemWriter<Long> eventWriter() {
        log.info("writer");
        return childProfileIds -> {
            Set<Long> uniqueProfileIds = new HashSet<>(childProfileIds.getItems());

            uniqueProfileIds.forEach(profileId -> {
                ChildProfile childProfile = childProfileService.getChildProfileWithMBTIScore(profileId);
                createAndUpdateHistory(childProfile);
            });

            redisUtil.deleteList(BOOK_LIKE_EVENT_LIST.getKey());
        };
    }

    private void createAndUpdateHistory(ChildProfile childProfile) {
        MBTIScore currentMBTIScore = MBTIScore.fromCumulativeScore(childProfile.getCumulativeMBTIScore());
        MBTI mbti = mbtiService.getMBTI(mbtiService.checkMBTIType(currentMBTIScore));
        currentMBTIScore.setMbti(mbti);

        ChildPersonalityHistory history = historyService.createHistory(
                childProfile.getId(),
                currentMBTIScore,
                HistoryCreatedType.FEEDBACK
        );

        List<GenreScore> genreScores = childProfile.getGenreScores();
        List<TopicScore> topicScores = childProfile.getTopicScores();
        historyService.updatePreferredGenresByScore(history, genreScores);
        historyService.updatePreferredTopicsByScore(history, topicScores);

        log.info("Create History - ChildProfile ID: {}", childProfile.getId());
    }
}
