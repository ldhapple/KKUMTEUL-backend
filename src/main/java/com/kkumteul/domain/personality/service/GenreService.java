package com.kkumteul.domain.personality.service;

import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.repository.GenreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GenreService {

    private final GenreRepository genreRepository;

    // 1. 장르 이름으로 장르 가져오기
    public Genre getGenre(String genre) {
        return genreRepository.findByGenre(genre);
    }
}
