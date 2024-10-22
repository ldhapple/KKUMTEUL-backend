package com.kkumteul.domain.book.service;

import com.kkumteul.domain.book.entity.BookMBTI;
import com.kkumteul.domain.book.repository.BookMBTIRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookMBTIService {
    private final BookMBTIRepository bookMbtiRepository;

    public BookMBTI insertBookMBTI(BookMBTI bookMBTI) {
        return bookMbtiRepository.save(bookMBTI);
    }
}
