package com.kkumteul.domain.childprofile.repository;

import com.kkumteul.domain.childprofile.entity.GenreScore;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreScoreRepository extends JpaRepository<GenreScore, Long> {
    List<GenreScore> findByChildProfileId(Long childProfileId);
}
