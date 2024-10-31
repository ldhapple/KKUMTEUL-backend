package com.kkumteul.domain.book.controller;

import com.kkumteul.domain.book.dto.GetBookDetailResponseDto;
import com.kkumteul.domain.book.dto.GetBookListResponseDto;
import com.kkumteul.domain.book.dto.GetLikeStatusResponse;
import com.kkumteul.domain.book.dto.PostBookLikeRequestDto;
import com.kkumteul.domain.book.entity.LikeType;
import com.kkumteul.domain.book.service.BookService;
import com.kkumteul.util.ApiUtil;
import com.kkumteul.util.ApiUtil.ApiSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    // 전체 도서 목록 조회
    @GetMapping
    public ApiSuccess<?> getBookList(
            @RequestParam(value = "keyword", required = false) final String keyword,
            final Pageable pageable){

        // 검색 키워드 있을 경우
        if(keyword != null && !keyword.isEmpty()){
            Page<GetBookListResponseDto> bookList = bookService.getBookList(keyword, pageable);
            return ApiUtil.success(bookList);
        }
        // 검색 키워드 없는 경우
        Page<GetBookListResponseDto> bookList = bookService.getBookList(pageable);
        return ApiUtil.success(bookList);
    }

    // 상세 도서 조회
    @GetMapping("/{bookId}")
    public ApiSuccess<?> getBookDetail(@PathVariable("bookId") final Long bookId){

        GetBookDetailResponseDto bookDetail = bookService.getBookDetail(bookId);

        return ApiUtil.success(bookDetail);
    }

    // 좋아요, 싫어요 처리
    @PostMapping("/like")
    public ApiSuccess<?> bookLike(@RequestBody PostBookLikeRequestDto bookLikeRequestDto){

        LikeType likeType = bookLikeRequestDto.getLikeType();

        bookService.bookLike(bookLikeRequestDto.getBookId(), bookLikeRequestDto.getChildProfileId(), likeType);

        return ApiUtil.success(likeType == LikeType.LIKE ? "좋아요 처리를 완료했습니다." : "싫어요 처리를 완료했습니다.");
    }

    @GetMapping("/like")
    public ApiSuccess<?> getLikeStatus(
            @RequestParam Long bookId,
            @RequestParam Long childProfileId) {
        Map<String, Boolean> likeStatus = bookService.checkLikeStatus(bookId, childProfileId);

        GetLikeStatusResponse response = new GetLikeStatusResponse(
                likeStatus.get("isLiked"),
                likeStatus.get("isDisliked")
        );

        return ApiUtil.success(response);
    }

}
