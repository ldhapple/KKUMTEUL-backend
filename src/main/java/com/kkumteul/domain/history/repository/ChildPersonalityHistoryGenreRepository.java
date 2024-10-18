package com.kkumteul.domain.history.repository;

import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.history.entity.ChildPersonalityHistoryGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildPersonalityHistoryGenreRepository extends JpaRepository<ChildPersonalityHistoryGenre, Long> {
    List<ChildPersonalityHistoryGenre> findByHistoryId(Long id);
}
