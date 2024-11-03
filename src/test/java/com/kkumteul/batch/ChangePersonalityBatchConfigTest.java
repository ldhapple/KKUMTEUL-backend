package com.kkumteul.batch;

import static com.kkumteul.util.redis.RedisKey.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.kkumteul.config.scheduler.ChangePersonalityScheduler;
import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.service.BookService;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.CumulativeMBTIScore;
import com.kkumteul.domain.childprofile.service.ChildProfileService;
import com.kkumteul.domain.childprofile.service.PersonalityScoreService;
import com.kkumteul.domain.history.entity.HistoryCreatedType;
import com.kkumteul.domain.history.entity.MBTIScore;
import com.kkumteul.domain.history.service.ChildPersonalityHistoryService;
import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.mbti.entity.MBTIName;
import com.kkumteul.domain.mbti.repository.MBTIRepository;
import com.kkumteul.domain.mbti.service.MBTIService;
import com.kkumteul.util.redis.RedisKey;
import com.kkumteul.util.redis.RedisUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@SpringBatchTest
public class ChangePersonalityBatchConfigTest {

    @MockBean
    private RedisUtil redisUtil;

    @MockBean
    private ChildProfileService childProfileService;

    @MockBean
    private PersonalityScoreService personalityScoreService;

    @MockBean
    private ChildPersonalityHistoryService historyService;

    @MockBean
    private BookService bookService;

    @MockBean
    private MBTIService mbtiService;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job processLikeDislikeEventsJob;

    @Autowired
    private ChangePersonalityScheduler changePersonalityScheduler;

    @BeforeEach
    public void setUp() {
        jobLauncherTestUtils.setJob(processLikeDislikeEventsJob);
    }

    @Test
    @DisplayName("ChangePersonality 배치 작업 테스트")
    public void testChangePersonalityJob() throws Exception {
        List<String> mockEvents = List.of("1:101:LIKE", "2:202:DISLIKE");
        given(redisUtil.getAllFromList(BOOK_LIKE_EVENT_LIST.getKey())).willReturn(new ArrayList<>(mockEvents));

        for (String event : mockEvents) {
            String[] data = event.split(":");
            Long childProfileId = Long.parseLong(data[0]);
            Long bookId = Long.parseLong(data[1]);

            ChildProfile mockChildProfile = mock(ChildProfile.class);
            Book mockBook = mock(Book.class);
            CumulativeMBTIScore mockScore = mock(CumulativeMBTIScore.class);
            MBTI mockMBTI = mock(MBTI.class);

            given(childProfileService.getChildProfileWithMBTIScore(childProfileId)).willReturn(mockChildProfile);
            given(bookService.getBook(bookId)).willReturn(mockBook);
            given(mockChildProfile.getCumulativeMBTIScore()).willReturn(mockScore);
            given(mbtiService.getMBTI(anyString())).willReturn(mockMBTI);
        }

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("ChangePersonalityJob", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.getJobLauncher()
                .run(jobLauncherTestUtils.getJob(), jobParameters);

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        verify(redisUtil, times(1)).deleteList(BOOK_LIKE_EVENT_LIST.getKey());
        verify(personalityScoreService, atLeastOnce()).updateGenreAndTopicScores(any(ChildProfile.class),
                any(Book.class), anyDouble());
        verify(historyService, atLeastOnce()).createHistory(anyLong(), any(MBTIScore.class),
                any(HistoryCreatedType.class));
    }

    @Test
    @DisplayName("ChangePersonality 스케줄러 메서드 수행 테스트")
    public void testSchedulerJobExecution() throws Exception {
        changePersonalityScheduler.runChangePersonalityJob();

        verify(redisUtil, atLeastOnce()).getAllFromList(anyString());
    }
}
