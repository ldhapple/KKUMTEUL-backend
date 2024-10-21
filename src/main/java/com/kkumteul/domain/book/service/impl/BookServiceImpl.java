package com.kkumteul.domain.book.service.impl;

import com.kkumteul.domain.book.dto.GetBookListResponseDto;
import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.repository.BookRepository;
import com.kkumteul.domain.book.service.BookService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Transactional
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    // 전체 도서 목록 조회
    @Override
    public Page<GetBookListResponseDto> getBookList(final Pageable pageable) {
        final Page<Book> books = bookRepository.findAllBookInfo(pageable);

        return books.map(GetBookListResponseDto::from);
    }

}
