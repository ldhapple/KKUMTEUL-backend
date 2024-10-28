package com.kkumteul.domain.childprofile.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.CumulativeMBTIScore;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class CumulativeMBTIScoreRepositoryTest {

    @Autowired
    private CumulativeMBTIScoreRepository cumulativeMBTIScoreRepository;

    @Autowired
    private ChildProfileRepository childProfileRepository;

    @Test
    @DisplayName("자녀 프로필 ID로 누적 MBTI 점수 조회 테스트")
    void testFindByChildProfileId() {
        ChildProfile childProfile = childProfileRepository.save(ChildProfile.builder()
                .name("lee")
                .build());

        CumulativeMBTIScore cumulativeScore = CumulativeMBTIScore.builder()
                .iScore(1.0)
                .eScore(2.0)
                .childProfile(childProfile)
                .build();

        cumulativeMBTIScoreRepository.save(cumulativeScore);

        Optional<CumulativeMBTIScore> foundScore = cumulativeMBTIScoreRepository.findByChildProfileId(childProfile.getId());

        assertThat(foundScore).isPresent();
        assertThat(foundScore.get().getIScore()).isEqualTo(1.0);
        assertThat(foundScore.get().getEScore()).isEqualTo(2.0);
    }
}