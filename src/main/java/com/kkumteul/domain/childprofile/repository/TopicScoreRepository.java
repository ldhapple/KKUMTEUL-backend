package com.kkumteul.domain.childprofile.repository;

import com.kkumteul.domain.childprofile.entity.TopicScore;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicScoreRepository extends JpaRepository<TopicScore, Long> {

    List<TopicScore> findByChildProfileId(Long childProfileId);
}
