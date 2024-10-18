package com.kkumteul.domain.childprofile.repository;

import com.kkumteul.domain.childprofile.entity.TopicScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicScoreRepository extends JpaRepository<TopicScore, Long> {
}
