package com.kkumteul.domain.childprofile.repository;

import com.kkumteul.domain.childprofile.entity.GenreScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreScoreRepository extends JpaRepository<GenreScore, Long> {
}
