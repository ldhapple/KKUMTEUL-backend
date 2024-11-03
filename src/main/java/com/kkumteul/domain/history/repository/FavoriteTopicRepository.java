package com.kkumteul.domain.history.repository;

import com.kkumteul.domain.history.entity.FavoriteTopic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteTopicRepository extends JpaRepository<FavoriteTopic, Long> {
}
