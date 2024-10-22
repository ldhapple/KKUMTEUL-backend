package com.kkumteul.domain.personality.repository;

import com.kkumteul.domain.personality.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
