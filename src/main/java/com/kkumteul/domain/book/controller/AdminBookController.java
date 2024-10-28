package com.kkumteul.domain.book.controller;

import com.kkumteul.domain.book.dto.AdminGetBookDetailResponseDto;
import com.kkumteul.domain.book.dto.AdminGetBookListResponseDto;
import com.kkumteul.domain.book.dto.AdminBookRequestDto;
import com.kkumteul.domain.book.service.BookService;
import com.kkumteul.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.kkumteul.util.ApiUtil.ApiSuccess;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/books")
public class AdminBookController {

    private final BookService bookService;

    // 1. 도서 등록
    @PostMapping
    public ApiSuccess<?> insertBook(
            @RequestPart(value = "image") MultipartFile image,
            @RequestPart(value = "book") AdminBookRequestDto adminInsertBookRequestDto) {

        bookService.insertBook(adminInsertBookRequestDto, image);

        return ApiUtil.success("book insert successfully");
    }

    // 2. 전체 도서 목록 조회
    @GetMapping
    public ApiSuccess<?> getAdminBookList(final Pageable pageable) {

        Page<AdminGetBookListResponseDto> bookList = bookService.getAdminBookList(pageable);

        return ApiUtil.success(bookList);
    }

    // 3. 도서 상세 조회
    @GetMapping("/{bookId}")
    public ApiSuccess<?> getAdminBookDetail(@PathVariable("bookId") final Long bookId) {

        AdminGetBookDetailResponseDto bookDetail = bookService.getBookDetailById(bookId);

        return ApiUtil.success(bookDetail);
    }

    // 4. 도서 수정
    @PutMapping("/{bookId}")
    public ApiSuccess<?> updateBook(
            @PathVariable("bookId") final Long bookId,
            @RequestPart(value = "image") MultipartFile image,
            @RequestPart(value = "book") AdminBookRequestDto adminUpdateBookRequestDto) {

        bookService.updateBook(bookId, adminUpdateBookRequestDto, image);

        return ApiUtil.success("book update successfully");
    }

    // 5. 도서 삭제
    @DeleteMapping("/{bookId}")
    public ApiSuccess<?> deleteBook(@PathVariable("bookId") final Long bookId) {
        bookService.deleteBook(bookId);

        return ApiUtil.success("book delete successfully");
    }

    // 6. 도서 검색어 조회
    @GetMapping("/search")
    public ApiSuccess<?> getSearchBookList(
            @RequestParam(name = "search", required = false) String keyword, final Pageable pageable) {

        Page<AdminGetBookListResponseDto> booksList;
        if (keyword == null || keyword.isEmpty()) {
            // 검색어가 없을 때는 전체 도서 목록 조회
            booksList = bookService.getAdminBookList(pageable);
        } else {
            // 검색어가 있을 때는 해당 검색어로 조회
            booksList = bookService.getSearchBookList(keyword, pageable);
        }

        return ApiUtil.success(booksList);
    }
}