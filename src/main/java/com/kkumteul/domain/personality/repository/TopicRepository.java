package com.kkumteul.domain.personality.repository;

import com.kkumteul.domain.personality.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
