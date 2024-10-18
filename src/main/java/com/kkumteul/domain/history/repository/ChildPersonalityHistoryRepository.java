package com.kkumteul.domain.history.repository;

import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildPersonalityHistoryRepository extends JpaRepository<ChildPersonalityHistory, Long> {

    @Query("select h from ChildPersonalityHistory h where h.childProfile.id = :id order by h.createdAt desc")
    ChildPersonalityHistory findByProfileId(Long id);
}
