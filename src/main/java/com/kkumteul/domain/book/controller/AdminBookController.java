package com.kkumteul.domain.book.controller;

import com.kkumteul.domain.book.dto.AdminInsertBookRequestDto;
import com.kkumteul.domain.book.service.BookService;
import com.kkumteul.util.ApiUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.kkumteul.util.ApiUtil.ApiSuccess;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/books")
public class AdminBookController {

    private final BookService bookService;

    // 1. 도서 등록
    @PostMapping
    public ApiSuccess<?> insertBook(@Valid @RequestBody AdminInsertBookRequestDto adminInsertBookRequestDto) {
        bookService.insertBook(adminInsertBookRequestDto);

        return ApiUtil.success("book insert successfully");
    }
}