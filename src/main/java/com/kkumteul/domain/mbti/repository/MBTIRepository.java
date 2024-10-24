package com.kkumteul.domain.mbti.repository;

import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.mbti.entity.MBTIName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MBTIRepository extends JpaRepository<MBTI, Long> {

    Optional<MBTI> findByMbti(MBTIName mbtiName);
}
