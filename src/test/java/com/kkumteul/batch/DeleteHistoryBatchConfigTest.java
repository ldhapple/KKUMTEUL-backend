package com.kkumteul.batch;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.kkumteul.config.DeleteHistoryBatchTestConfig;
import com.kkumteul.config.scheduler.DeleteHistoryScheduler;
import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.history.repository.ChildPersonalityHistoryRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@SpringBatchTest
public class DeleteHistoryBatchConfigTest {

    @MockBean
    private ChildPersonalityHistoryRepository historyRepository;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private DeleteHistoryScheduler deleteHistoryScheduler;

    @Autowired
    private Job deleteHistoryJob;

    @BeforeEach
    public void setUp() {
        jobLauncherTestUtils.setJob(deleteHistoryJob);
    }

    @Test
    @DisplayName("배치 작업 테스트")
    public void testDeleteHistoryJob() throws Exception {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<ChildPersonalityHistory> histories = new ArrayList<>();

        given(historyRepository.findAllByRealDelete(oneMonthAgo)).willReturn(histories);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();

        //배치 작업 직접 실행 (jobParameter 전달)
        JobExecution jobExecution = jobLauncherTestUtils.getJobLauncher().run(jobLauncherTestUtils.getJob(), jobParameters);

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        verify(historyRepository, times(1)).deleteAll(histories);
    }

    @Test
    @DisplayName("스케줄러 메서드 수행 테스트")
    public void testSchedulerJobExecution() throws Exception {
        //4시까지 기다리지 않고 스케줄러 메서드를 수동으로 실행한다.
        deleteHistoryScheduler.runDeleteHistoryJob();

        verify(historyRepository, atLeastOnce()).findAllByRealDelete(any(LocalDateTime.class));
        verify(historyRepository, atLeastOnce()).deleteAll(anyList());
    }
}
