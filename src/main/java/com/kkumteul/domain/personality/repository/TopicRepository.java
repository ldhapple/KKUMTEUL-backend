package com.kkumteul.domain.personality.repository;

import com.kkumteul.domain.personality.entity.Topic;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    Topic findByName(String topic);

    @Query("SELECT t.name FROM Topic t")
    List<String> findAllTopicNames();
}
