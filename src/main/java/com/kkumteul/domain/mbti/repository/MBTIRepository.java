package com.kkumteul.domain.mbti.repository;

import com.kkumteul.domain.mbti.entity.MBTI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MBTIRepository extends JpaRepository<MBTI, Long> {
}
