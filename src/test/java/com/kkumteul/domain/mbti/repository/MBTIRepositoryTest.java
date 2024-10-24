package com.kkumteul.domain.mbti.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.mbti.entity.MBTIName;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MBTIRepositoryTest {

    @Autowired
    private MBTIRepository mbtiRepository;

    @Test
    @DisplayName("MBTI로 조회하기")
    void testFindByMBTISuccess() {
        MBTI mbti = MBTI.builder()
                .mbti(MBTIName.ENFJ)
                .title("수호자")
                .description("활발한 사람")
                .build();
        mbtiRepository.save(mbti);

        Optional<MBTI> result = mbtiRepository.findByMbti(MBTIName.ENFJ);

        assertThat(result).isPresent();
        assertThat(result.get().getDescription()).isEqualTo("활발한 사람");
    }
}