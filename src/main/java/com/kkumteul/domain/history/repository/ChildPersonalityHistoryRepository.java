package com.kkumteul.domain.history.repository;

import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.history.entity.FavoriteGenre;
import com.kkumteul.domain.history.entity.FavoriteTopic;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.kkumteul.domain.history.entity.HistoryCreatedType;
import com.kkumteul.domain.recommendation.dto.ChildDataDto;

import java.util.List;
import java.util.Optional;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface ChildPersonalityHistoryRepository extends JpaRepository<ChildPersonalityHistory, Long> {

    // 자녀 프로필 ID(외래키)로 자녀 성향 히스토리 테이블(ChildPersonalityHistoryRepository)에서 가장 최신 히스토리의 ID(기본키)를 찾고
    // 히스토리 ID로 자녀 성향 히스토리_선호 주제어 테이블(ChildPersonalityHistoryTopicRepository)이랑
    // 자녀 성향 히스토리_선호 장르 테이블(ChildPersonalityHistoryGenreRepository)에서 히스토리 ID(외래키)로 주제어 ID와 장르 ID를 찾아 이름과 함께 반환
    // 그 히스토리에 있는 MBTI 점수 ID(외래키)로 MBTI 점수 테이블(MBTIScoreRepository)에서 MBTI ID(외래키)값을 가져와서 MBTI 테이블(MBTIRepository)에서 해당 MBTI를 반환
    // 위에 내용 한번에 조회하기

    @EntityGraph(attributePaths = {
            "childProfile",
            "mbtiScore.mbti"
    })
    @Query("""
        SELECT ch 
        FROM ChildPersonalityHistory ch
        WHERE ch.childProfile.id = :profileId AND ch.isDeleted = false
        ORDER BY ch.createdAt DESC
    """)
    Page<ChildPersonalityHistory> findChildData(@Param("profileId") Long profileId, Pageable pageable);

    @EntityGraph(attributePaths = {
            "childProfile",  // 사용자 프로필 로딩
            "mbtiScore.mbti" // MBTI 점수와 MBTI 정보 로딩
    })
    @Query("""
        SELECT ch 
        FROM ChildPersonalityHistory ch
        WHERE ch.isDeleted = false 
          AND ch.createdAt = (
            SELECT MAX(ch2.createdAt) 
            FROM ChildPersonalityHistory ch2 
            WHERE ch2.childProfile.id = ch.childProfile.id
          )
        ORDER BY ch.createdAt DESC
    """)
    List<ChildPersonalityHistory> findAllChildrenData();

    ChildPersonalityHistory findTopByChildProfileIdOrderByCreatedAtDesc(Long childProfileId);

    @Query("SELECT h FROM ChildPersonalityHistory h WHERE h.childProfile.id = :childProfileId AND h.historyCreatedType = :historyCreatedType")
    Optional<ChildPersonalityHistory> findHistoryByChildProfileIdAndHistoryCreatedType(Long childProfileId, HistoryCreatedType historyCreatedType);

    @Query("SELECT h FROM ChildPersonalityHistory h JOIN FETCH h.mbtiScore WHERE h.id = :historyId")
    Optional<ChildPersonalityHistory> findByIdWithMbtiScore(Long historyId);
  
    @Query("SELECT h FROM ChildPersonalityHistory  h JOIN FETCH h.mbtiScore ms JOIN FETCH ms.mbti m WHERE h.childProfile.id = :childProfileId AND h.isDeleted = false")
    List<ChildPersonalityHistory> findHistoryWithMBTIByChildProfileId(@Param("childProfileId") Long childProfileId);

    @Query("SELECT h FROM ChildPersonalityHistory h WHERE h.isDeleted = true AND h.deletedAt < :dateTime")
    List<ChildPersonalityHistory> findAllByRealDelete(@Param("dateTime") LocalDateTime dateTime);
}
