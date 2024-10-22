package com.kkumteul.domain.personality.service;

import com.kkumteul.domain.book.entity.BookGenre;
import com.kkumteul.domain.book.repository.BookGenreRepository;
import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.repository.GenreRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
//@RequiredArgsConstructor
@AllArgsConstructor
@Transactional
public class GenreService {

    private GenreRepository genreRepository;

    // 1. 장르 등록
    public Genre insertGenre(Genre genre) {
        return genreRepository.save(genre);
    }
}
