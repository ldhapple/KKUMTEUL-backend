package com.kkumteul.domain.book.controller;

import com.kkumteul.domain.book.dto.GetBookDetailResponseDto;
import com.kkumteul.domain.book.dto.GetBookListResponseDto;
import com.kkumteul.domain.book.service.BookService;
import com.kkumteul.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    // 전체 도서 목록 조회
    @GetMapping
    public ApiUtil.ApiSuccess<?> getBookList(final Pageable pageable){

        Page<GetBookListResponseDto> bookList = bookService.getBookList(pageable);

        return ApiUtil.success(bookList);
    }
}
