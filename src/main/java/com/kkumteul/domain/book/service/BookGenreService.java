package com.kkumteul.domain.book.service;

import com.kkumteul.domain.book.entity.BookGenre;
import com.kkumteul.domain.book.repository.BookGenreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookGenreService {

    private BookGenreRepository bookGenreRepository;

    // 1. 장르 등록
    public BookGenre insertBookGenre(BookGenre bookGenre) {
        return bookGenreRepository.save(bookGenre);
    }
}
