package com.kkumteul.domain.history.repository;

import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.history.entity.HistoryCreatedType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChildPersonalityHistoryRepository extends JpaRepository<ChildPersonalityHistory, Long> {
    ChildPersonalityHistory findTopByChildProfileIdOrderByCreatedAtDesc(Long childProfileId);

    @Query("SELECT h FROM ChildPersonalityHistory h WHERE h.childProfile.id = :childProfileId AND h.historyCreatedType = :historyCreatedType")
    Optional<ChildPersonalityHistory> findHistoryByChildProfileIdAndHistoryCreatedType(Long childProfileId,
                                                                                       HistoryCreatedType historyCreatedType);
}
