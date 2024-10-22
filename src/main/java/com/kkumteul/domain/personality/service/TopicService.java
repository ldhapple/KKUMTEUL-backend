package com.kkumteul.domain.personality.service;

import com.kkumteul.domain.personality.entity.Topic;
import com.kkumteul.domain.personality.repository.TopicRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
//@RequiredArgsConstructor
@AllArgsConstructor
@Transactional
public class TopicService {

    private TopicRepository topicRepository;

    // 1. 주제어 등록
    public Topic insertTopic(Topic topic) {
        return topicRepository.save(topic);
    }
}
