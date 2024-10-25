package com.kkumteul.domain.book.service.impl;

import com.kkumteul.domain.book.dto.GetBookListResponseDto;
import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.repository.BookRepository;
import com.kkumteul.domain.book.service.BookService;
import com.kkumteul.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    // 전체 도서 목록 조회
    @Override
    @Transactional(readOnly = true)
    public Page<GetBookListResponseDto> getBookList(final Pageable pageable) {
        final Page<Book> books = bookRepository.findAllBookInfo(pageable);

        return books.map(GetBookListResponseDto::from);
    }

    @Override
    @Cacheable(value = "book", key = "#bookId")
    @Transactional(readOnly = true)
    public Book getBookWithCache(Long bookId) {
        log.info("get Book - bookID: {}", bookId);
        return bookRepository.findBookByIdWithGenreAndTopic(bookId)
                .orElseThrow(() -> new EntityNotFoundException(bookId));
    }
}
