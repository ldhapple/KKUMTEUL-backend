package com.kkumteul.domain.mbti.repository;

import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.mbti.entity.MBTIName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MBTIRepository extends JpaRepository<MBTI, Long> {

    Optional<MBTI> findByMbti(MBTIName mbtiName);
}
