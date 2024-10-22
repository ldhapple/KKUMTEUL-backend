package com.kkumteul.domain.book.service;

import com.kkumteul.domain.book.entity.BookTopic;
import com.kkumteul.domain.book.repository.BookTopicRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookTopicService {
    private final BookTopicRepository bookTopicRepository;

    public BookTopic insertBookTopic(BookTopic bookTopic) {
        return bookTopicRepository.save(bookTopic);
    }
}
