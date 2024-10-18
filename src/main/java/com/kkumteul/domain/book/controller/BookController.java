package com.kkumteul.domain.book.controller;

import com.kkumteul.domain.book.dto.GetBookListResponse;
import com.kkumteul.domain.book.service.BookService;
import com.kkumteul.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    // 전체 도서 목록 조회
    @GetMapping
    public ApiUtil.ApiSuccess<?> getBookList(){

        List<GetBookListResponse> bookList = bookService.getBookList();

        return ApiUtil.success(bookList);
    }
}
