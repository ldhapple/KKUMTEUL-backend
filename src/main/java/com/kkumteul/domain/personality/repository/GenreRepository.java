package com.kkumteul.domain.personality.repository;

import com.kkumteul.domain.personality.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    // 입력받은 장르 이름과 일치하는 데이터를 검색하는 메소드
    Genre findByName(String genre);
}
