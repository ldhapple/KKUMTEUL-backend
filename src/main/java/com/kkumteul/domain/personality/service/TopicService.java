package com.kkumteul.domain.personality.service;

import com.kkumteul.domain.personality.entity.Topic;
import com.kkumteul.domain.personality.repository.TopicRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TopicService {

    private final TopicRepository topicRepository;

    // 1. 주제어 이름으로 주제어 객체 가져오기
    public Topic getTopic(String topic) {
        return topicRepository.findByTopic(topic);
    }
}
