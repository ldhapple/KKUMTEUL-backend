package com.kkumteul.domain.book.controller;

import com.kkumteul.domain.book.dto.AdminGetBookDetailResponseDto;
import com.kkumteul.domain.book.dto.AdminGetBookListResponseDto;
import com.kkumteul.domain.book.dto.AdminInsertBookRequestDto;
import com.kkumteul.domain.book.service.BookService;
import com.kkumteul.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.kkumteul.util.ApiUtil.ApiSuccess;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/books")
public class AdminBookController {

    private final BookService bookService;

    // 1. 관리자의 도서 등록
    @PostMapping
    public ApiSuccess<?> insertBook(
            @RequestPart(value = "image") MultipartFile image,
            @RequestPart(value = "book") AdminInsertBookRequestDto adminInsertBookRequestDto) {

        bookService.insertBook(adminInsertBookRequestDto, image);

        return ApiUtil.success("book insert successfully");
    }

    // 2. 관리자의 전체 도서 목록 조회
    @GetMapping
    public ApiSuccess<?> getAdminBookList(final Pageable pageable) {

        Page<AdminGetBookListResponseDto> bookList = bookService.getAdminBookList(pageable);

        return ApiUtil.success(bookList);
    }

    // 3. 관리자의 도서 상세 조회
    @GetMapping("/{bookId}")
    public ApiSuccess<?> getAdminBookDetail(@PathVariable("bookId") final Long bookId) {

        AdminGetBookDetailResponseDto bookDetail = bookService.getBookDetailById(bookId);

        return ApiUtil.success(bookDetail);
    }
}