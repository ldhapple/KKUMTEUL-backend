package com.kkumteul.domain.recommendation.controller;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.recommendation.dto.ChildDataDto;
import com.kkumteul.domain.recommendation.dto.RecommendBookDto;
import com.kkumteul.domain.recommendation.service.RecommendationService;
import com.kkumteul.util.ApiUtil;
import com.kkumteul.util.ApiUtil.ApiSuccess;

import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    // 자녀 아이디 있을 때(로그인 되어있을 때)
    @GetMapping("/books")
    public ApiSuccess<?> getRecommendedBooks(@RequestParam(name = "child", required = false) Long childProfileId) {

        List<RecommendBookDto> popularBooks = recommendationService.getPopularRecommendations(); // 좋아요 순 인기도서 5

        List<RecommendBookDto> recommendedBooks = new ArrayList<>();

        if(childProfileId != null && childProfileId > 0) {
            recommendedBooks = recommendationService.getRecommendedBooks(childProfileId); // Redis에서 먼저 조회하고 없으면 DB에서 가져와 Redis에 저장
            recommendationService.updateLastActivity(childProfileId);

        } else{
            List<Book> bookList = recommendationService.getDefaultRecommendations(10); // 기본 추천 - 10살대 추천 도서
            Collections.shuffle(bookList);
            bookList = bookList.subList(0, Math.min(5, bookList.size())); // 랜덤 5권

            recommendedBooks= bookList.stream()
                    .map(RecommendBookDto::fromEntity)
                    .toList();
        }

        Map<String, List<RecommendBookDto>> finalBooks = new HashMap<>();
        finalBooks.put("recommendedBooks", recommendedBooks);
        finalBooks.put("popularBooks", popularBooks);

        return ApiUtil.success(finalBooks);
    }
}
