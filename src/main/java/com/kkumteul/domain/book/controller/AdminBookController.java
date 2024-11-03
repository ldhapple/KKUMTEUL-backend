package com.kkumteul.domain.book.controller;

import com.kkumteul.domain.book.dto.AdminBookFilterResponseDto;
import com.kkumteul.domain.book.dto.AdminGetBookDetailResponseDto;
import com.kkumteul.domain.book.dto.AdminGetBookListResponseDto;
import com.kkumteul.domain.book.dto.AdminBookRequestDto;
import com.kkumteul.domain.book.service.AdminBookService;
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

    private final AdminBookService bookService;

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

    // 7. 장르, 주제어, MBTI 필터 결과 조회
    @GetMapping("/filter/all")
    public ApiSuccess<?> getFilterBookListGenreTopicMBTI(
            @RequestParam(name = "genre", required = false) String genre,
            @RequestParam(name = "topic", required = false) String topic,
            @RequestParam(name = "mbti", required = false) String mbti,
            final Pageable pageable){

        Page<AdminBookFilterResponseDto> booksList;
        Page<AdminGetBookListResponseDto> bookListAll;
        if (genre == null || genre.isEmpty() && topic == null || topic.isEmpty() && mbti == null || mbti.isEmpty()) {
            // 아무것도 필터링 되지 않은 경우에는 전체 도서 목록 조회
            bookListAll = bookService.getAdminBookList(pageable);
            return ApiUtil.success(bookListAll);
        } else {
            // 하나라도 필터링이 걸려있을 때는 필터링 동작
            booksList = bookService.filterBooksGenreTopicMBTI(genre, topic, mbti, pageable);
            return ApiUtil.success(booksList);

        }
    }

    // 7-2. 장르 필터 결과 조회
    @GetMapping("/filter/genre")
    public ApiSuccess<?> getFilterBookListGenre(
            @RequestParam(name = "genre", required = false) String genre,
            final Pageable pageable){

        Page<AdminBookFilterResponseDto> booksList;
        Page<AdminGetBookListResponseDto> bookListAll;
        if (genre == null || genre.isEmpty()) {
            // 아무것도 필터링 되지 않은 경우에는 전체 도서 목록 조회
            bookListAll = bookService.getAdminBookList(pageable);
            return ApiUtil.success(bookListAll);
        } else {
            // 하나라도 필터링이 걸려있을 때는 필터링 동작
            booksList = bookService.filterBooksGenre(genre, pageable);
            return ApiUtil.success(booksList);

        }
    }

    // 7-3. 주제어(topic) 필터 결과 조회
    @GetMapping("/filter/topic")
    public ApiSuccess<?> getFilterBookListTopic(
            @RequestParam(name = "topic", required = false) String topic,
            final Pageable pageable){

        Page<AdminBookFilterResponseDto> booksList;
        Page<AdminGetBookListResponseDto> bookListAll;
        if (topic == null || topic.isEmpty()) {
            // 아무것도 필터링 되지 않은 경우에는 전체 도서 목록 조회
            bookListAll = bookService.getAdminBookList(pageable);
            return ApiUtil.success(bookListAll);
        } else {
            // 하나라도 필터링이 걸려있을 때는 필터링 동작
            booksList = bookService.filterBooksTopic(topic, pageable);
            return ApiUtil.success(booksList);

        }
    }

    // 7-4. mbti 필터 결과 조회
    @GetMapping("/filter/mbti")
    public ApiSuccess<?> getFilterBookListMBTI(
            @RequestParam(name = "mbti", required = false) String mbti,
            final Pageable pageable){

        Page<AdminBookFilterResponseDto> booksList;
        Page<AdminGetBookListResponseDto> bookListAll;
        if (mbti == null || mbti.isEmpty()) {
            // 아무것도 필터링 되지 않은 경우에는 전체 도서 목록 조회
            bookListAll = bookService.getAdminBookList(pageable);
            return ApiUtil.success(bookListAll);
        } else {
            // 하나라도 필터링이 걸려있을 때는 필터링 동작
            booksList = bookService.filterBooksMBTI(mbti, pageable);
            return ApiUtil.success(booksList);

        }
    }

    // 7-5. 장르, 주제어 필터 결과 조회
    @GetMapping("/filter/genreandtopic")
    public ApiSuccess<?> getFilterBookListGenreTopic(
            @RequestParam(name = "genre", required = false) String genre,
            @RequestParam(name = "topic", required = false) String topic,
            final Pageable pageable){

        Page<AdminBookFilterResponseDto> booksList;
        Page<AdminGetBookListResponseDto> bookListAll;
        if (genre == null || genre.isEmpty() && topic == null || topic.isEmpty()) {
            // 아무것도 필터링 되지 않은 경우에는 전체 도서 목록 조회
            bookListAll = bookService.getAdminBookList(pageable);
            return ApiUtil.success(bookListAll);
        } else {
            // 하나라도 필터링이 걸려있을 때는 필터링 동작
            booksList = bookService.filterBooksGenreTopic(genre, topic, pageable);
            return ApiUtil.success(booksList);

        }
    }

    // 7-6. 장르, MBTI 필터 결과 조회
    @GetMapping("/filter/genreandmbti")
    public ApiSuccess<?> getFilterBookListGenreMBTI(
            @RequestParam(name = "genre", required = false) String genre,
            @RequestParam(name = "mbti", required = false) String mbti,
            final Pageable pageable){

        Page<AdminBookFilterResponseDto> booksList;
        Page<AdminGetBookListResponseDto> bookListAll;
        if (genre == null || genre.isEmpty() && mbti == null || mbti.isEmpty()) {
            // 아무것도 필터링 되지 않은 경우에는 전체 도서 목록 조회
            bookListAll = bookService.getAdminBookList(pageable);
            return ApiUtil.success(bookListAll);
        } else {
            // 하나라도 필터링이 걸려있을 때는 필터링 동작
            booksList = bookService.filterBooksGenreMBTI(genre, mbti, pageable);
            return ApiUtil.success(booksList);

        }
    }

    // 7-7. 주제어, MBTI 필터 결과 조회
    @GetMapping("/filter/topicandmbti")
    public ApiSuccess<?> getFilterBookListTopicMBTI(
            @RequestParam(name = "topic", required = false) String topic,
            @RequestParam(name = "mbti", required = false) String mbti,
            final Pageable pageable){

        Page<AdminBookFilterResponseDto> booksList;
        Page<AdminGetBookListResponseDto> bookListAll;
        if (topic == null || topic.isEmpty() && mbti == null || mbti.isEmpty()) {
            // 아무것도 필터링 되지 않은 경우에는 전체 도서 목록 조회
            bookListAll = bookService.getAdminBookList(pageable);
            return ApiUtil.success(bookListAll);
        } else {
            // 하나라도 필터링이 걸려있을 때는 필터링 동작
            booksList = bookService.filterBooksTopicMBTI(topic, mbti, pageable);
            return ApiUtil.success(booksList);

        }
    }
}