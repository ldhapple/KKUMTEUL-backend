package com.kkumteul.domain.history.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.history.entity.HistoryCreatedType;
import com.kkumteul.domain.history.entity.MBTIScore;
import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.mbti.entity.MBTIName;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ChildPersonalityHistoryRepositoryTest {

    @Autowired
    private ChildPersonalityHistoryRepository historyRepository;

    @Autowired
    private ChildProfileRepository childProfileRepository;

    @Test
    @DisplayName("최신 히스토리 조회 테스트")
    void testFindTopByChildProfileIdOrderByCreatedAtDesc() {
        ChildProfile childProfile = childProfileRepository.save(
                ChildProfile.builder()
                        .name("child1")
                        .build()
        );

        ChildPersonalityHistory history1 = ChildPersonalityHistory.builder()
                .childProfile(childProfile)
                .createdAt(LocalDateTime.now().minusDays(1))
                .historyCreatedType(HistoryCreatedType.DIAGNOSIS)
                .build();

        ChildPersonalityHistory history2 = ChildPersonalityHistory.builder()
                .childProfile(childProfile)
                .createdAt(LocalDateTime.now())
                .historyCreatedType(HistoryCreatedType.DIAGNOSIS)
                .build();

        historyRepository.save(history1);
        historyRepository.save(history2);

        ChildPersonalityHistory latestHistory = historyRepository.findTopByChildProfileIdOrderByCreatedAtDesc(childProfile.getId());

        assertThat(latestHistory).isNotNull();
        assertThat(latestHistory.getCreatedAt()).isAfter(history1.getCreatedAt());
    }

    @Test
    @DisplayName("특정 자녀 히스토리 조회 테스트")
    void testFindHistoryByChildProfileIdAndHistoryCreatedType() {
        ChildProfile childProfile = childProfileRepository.save(
                ChildProfile.builder()
                        .name("child2")
                        .build()
        );

        ChildPersonalityHistory history = ChildPersonalityHistory.builder()
                .childProfile(childProfile)
                .createdAt(LocalDateTime.now())
                .historyCreatedType(HistoryCreatedType.DIAGNOSIS)
                .build();

        historyRepository.save(history);

        Optional<ChildPersonalityHistory> foundHistory = historyRepository.findHistoryByChildProfileIdAndHistoryCreatedType(
                childProfile.getId(), HistoryCreatedType.DIAGNOSIS
        );

        assertThat(foundHistory).isPresent();
        assertThat(foundHistory.get().getHistoryCreatedType()).isEqualTo(HistoryCreatedType.DIAGNOSIS);
    }
}