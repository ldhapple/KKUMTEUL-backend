package com.kkumteul.domain.history.repository;

import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildPersonalityHistoryRepository extends JpaRepository<ChildPersonalityHistory, Long> {

    @Query("SELECT h FROM ChildPersonalityHistory  h LEFT JOIN FETCH h.mbtiScore ms LEFT JOIN FETCH ms.mbti m WHERE h.childProfile.id = :childProfileId")
    List<ChildPersonalityHistory> findHistoryWithMBTIByChildProfileId(@Param("childProfileId") Long childProfileId);

}
