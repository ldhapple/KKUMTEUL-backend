package com.kkumteul.domain.history.repository;

import com.kkumteul.domain.history.entity.FavoriteGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteGenreRepository extends JpaRepository<FavoriteGenre, Long> {
}
