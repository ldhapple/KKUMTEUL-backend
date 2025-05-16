package com.kkumteul.domain.childprofile.repository;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.TopicScore;
import com.kkumteul.domain.personality.entity.Topic;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TopicScoreRepository extends JpaRepository<TopicScore, Long> {

    List<TopicScore> findByChildProfileId(Long childProfileId);

    @Query("SELECT ts FROM TopicScore ts JOIN FETCH ts.topic WHERE ts.childProfile.id = :childProfileId AND ts.topic.id = :topicId")
    Optional<TopicScore> findByChildProfileAndTopic(@Param("childProfile") Long childProfileId, @Param("topicId") Long topicId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE TopicScore ts SET ts.score = ts.score + :delta " +
            "WHERE ts.childProfile.id = :childProfileId AND ts.topic.id = :topicId")
    int bulkUpdateScore(@Param("childProfileId") Long childProfileId,
                        @Param("topicId") Long topicId,
                        @Param("delta") Double delta);
}
