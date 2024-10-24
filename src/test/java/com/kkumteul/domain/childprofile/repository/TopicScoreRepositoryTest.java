package com.kkumteul.domain.childprofile.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.TopicScore;
import com.kkumteul.domain.personality.entity.Topic;
import com.kkumteul.domain.personality.repository.TopicRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class TopicScoreRepositoryTest {

    @Autowired
    private TopicScoreRepository topicScoreRepository;

    @Autowired
    private ChildProfileRepository childProfileRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Test
    @DisplayName("자녀 프로필 ID로 주제어 점수 조회 테스트")
    void testFindByChildProfileId() {
        ChildProfile childProfile = childProfileRepository.save(ChildProfile.builder()
                .name("lee")
                .build());

        Topic topic = topicRepository.save(Topic.builder().name("과학").build());

        TopicScore topicScore = TopicScore.builder()
                .topic(topic)
                .score(8.0)
                .build();
        topicScore.setChildProfile(childProfile);

        topicScoreRepository.save(topicScore);

        List<TopicScore> foundTopicScores = topicScoreRepository.findByChildProfileId(childProfile.getId());

        assertThat(foundTopicScores).isNotEmpty();
        assertThat(foundTopicScores.get(0).getScore()).isEqualTo(8.0);
        assertThat(foundTopicScores.get(0).getTopic().getName()).isEqualTo("과학");
    }
}