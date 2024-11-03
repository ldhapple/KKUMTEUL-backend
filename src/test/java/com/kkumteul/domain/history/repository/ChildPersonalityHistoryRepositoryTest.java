package com.kkumteul.domain.history.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.history.entity.HistoryCreatedType;
import com.kkumteul.domain.history.entity.MBTIScore;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
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

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("히스토리 ID로 히스토리 조회 테스트")
    void testGetHistoryById() {
        MBTIScore mbtiScore = MBTIScore.builder()
                .iScore(10)
                .build();
        em.persist(mbtiScore);

        ChildPersonalityHistory history = ChildPersonalityHistory.builder()
                .createdAt(LocalDateTime.now())
                .historyCreatedType(HistoryCreatedType.FEEDBACK)
                .mbtiScore(mbtiScore)
                .build();

        historyRepository.save(history);

        Optional<ChildPersonalityHistory> result = historyRepository.findByIdWithMbtiScore(history.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(history.getId());
        assertThat(result.get().getMbtiScore()).isNotNull();
    }

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
