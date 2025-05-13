package com.kkumteul.domain.test.controller;

import com.kkumteul.util.ApiUtil;
import com.kkumteul.util.ApiUtil.ApiSuccess;
import com.kkumteul.util.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
@Slf4j
public class BatchJobTestController {

    private final JobLauncher jobLauncher;
    private final Job processLikeDislikeEventsJob;
    private final RedisUtil redisUtil;
    private static final String REDIS_LIST_KEY = "BookLikeEventList";

    @PostMapping("/like")
    public ApiSuccess<?> runJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(processLikeDislikeEventsJob, jobParameters);
            return ApiUtil.success("배치 작업이 완료되었습니다.");
        } catch (Exception e) {
            return ApiUtil.success(e.getMessage());
        }
    }

    @PostMapping("/data")
    public void insertData() throws Exception {
        for (long childProfileId = 10008; childProfileId <= 10208; childProfileId++) {
            for (int i = 0; i < 5; i++) {
                long bookId = (long) (Math.random() * 800) + 1;
                String action = Math.random() < 0.5 ? "LIKE" : "DISLIKE";
                String event = childProfileId + ":" + bookId + ":" + action;
                redisUtil.pushToList(REDIS_LIST_KEY, event);
            }
        }

        log.info("insert data redis");
    }
}
