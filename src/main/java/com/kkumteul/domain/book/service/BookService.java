package com.kkumteul.domain.book.service;

import com.kkumteul.domain.book.dto.BookDto;
import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.repository.BookRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
//@RequiredArgsConstructor -> 레포지토리 인식이 안됨
@AllArgsConstructor
@Transactional
public class BookService {
    private BookRepository bookRepository;

    // 1. 도서 등록
    public BookDto insertBook(Book book) {

        Book insertedBook = bookRepository.save(book);
        BookDto insertedBookDto = BookDto.fromEntity(insertedBook);

        return insertedBookDto;
    }

    // 2. 도서 조회
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        BookDto getBookDto = BookDto.fromEntity(book);

        return getBookDto;
    }
}
