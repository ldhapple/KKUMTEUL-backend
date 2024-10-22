package com.kkumteul.domain.book.service;

import com.kkumteul.domain.book.dto.BookDto;
import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.repository.BookRepository;
import com.kkumteul.exception.BookNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
    private final BookRepository bookRepository;

    // 1. 도서 등록
    public BookDto insertBook(Book book) {

        Book insertedBook = bookRepository.save(book);
        BookDto insertedBookDto = BookDto.fromEntity(insertedBook);

        return insertedBookDto;
    }

    // 2. 도서 조회
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        BookDto getBookDto = BookDto.fromEntity(book);

        return getBookDto;
    }
}
