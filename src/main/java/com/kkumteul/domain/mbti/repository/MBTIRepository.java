package com.kkumteul.domain.mbti.repository;

import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.mbti.entity.MBTIName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MBTIRepository extends JpaRepository<MBTI, Long> {

    // 입력받은 mbti 이름과 일치하는 데이터를 검색하는 메소드
    Optional<MBTI> findByMbti(MBTIName mbtiName);
}
