package com.kkumteul.domain.mbti.repository;

import com.kkumteul.domain.mbti.entity.MBTIScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MBTIScoreRepository extends JpaRepository<MBTIScore, Long> {
}
