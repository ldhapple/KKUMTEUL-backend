package com.kkumteul.domain.book.service;

import static com.kkumteul.util.kafka.KafkaTopic.*;

import com.kkumteul.util.kafka.KafkaTopic;
import com.kkumteul.util.kafka.KafkaUtil;
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
public class BookLikeService {

    private final KafkaUtil kafkaUtil;

    public void likeBook(Long childProfileId, Long bookId) {
        String message = childProfileId + ":" + bookId + ":LIKE";
        kafkaUtil.sendMessage(BOOK_LIKE.getTopicName(), message);
        log.info("send msg Kafka - childProfileId: {}, bookId: {}", childProfileId, bookId);
    }

    public void dislikeBook(Long childProfileId, Long bookId) {
        String message = childProfileId + ":" + bookId + ":LIKE";
        kafkaUtil.sendMessage(BOOK_DISLIKE.getTopicName(), message);
        log.info("send msg Kafka - childProfileId: {}, bookId: {}", childProfileId, bookId);
    }
}
