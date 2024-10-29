package com.kkumteul.util.kafka;

import static com.kkumteul.util.kafka.KafkaTopic.*;
import static com.kkumteul.util.redis.RedisKey.*;

import com.kkumteul.util.redis.RedisKey;
import com.kkumteul.util.redis.RedisUtil;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventHandlingService {

    private final RedisUtil redisUtil;

    @KafkaListener(topics = {BOOK_LIKE_TOPIC, BOOK_DISLIKE_TOPIC}, groupId = "child-profile-group")
    @Transactional
    public void processEvent(String message) {
        String[] data = message.split(":");
        String childProfileId = data[0];
        String bookId = data[1];

        List<Object> events = redisUtil.getAllFromList(BOOK_LIKE_EVENT_LIST.getKey());
        events.removeIf(event -> ((String) event).startsWith(childProfileId + ":" + bookId + ":"));
        events.add(message);

        redisUtil.deleteList(BOOK_LIKE_EVENT_LIST.getKey());
        events.forEach(event -> redisUtil.pushToList(BOOK_LIKE_EVENT_LIST.getKey(), event));

        log.info("save event to redis: {}", message);
    }
}
