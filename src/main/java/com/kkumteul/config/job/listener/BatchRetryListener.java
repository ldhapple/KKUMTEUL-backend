package com.kkumteul.config.job.listener;

import com.kkumteul.util.redis.RedisUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BatchRetryListener implements RetryListener {

    private final RedisUtil redisUtil;

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
                                                 Throwable throwable) {
        if (context.getRetryCount() == 3) {
            String item = (String) context.getAttribute("item");
            redisUtil.pushList("BOOK_LIKE_EVENT_FAILED_LIST", List.of(item));
            log.error("3회 재시도 실패 - item: {}", item, throwable);
        }
    }
}
