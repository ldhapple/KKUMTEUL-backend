package com.kkumteul.domain.recommendation.controller;

import com.kkumteul.domain.recommendation.dto.RecommendBookDto;
import com.kkumteul.domain.recommendation.service.RecommendationService;
import com.kkumteul.util.ApiUtil;
import com.kkumteul.util.ApiUtil.ApiSuccess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/books/{childProfileId}")
    public ApiSuccess<?> getRecommendedBooks(@PathVariable(name = "childProfileId") Long childProfileId) {
        //추후 JWT 토큰 구현되면, profileId를 가져오는 방식 변경 (PathVariable 사용 X)

        List<RecommendBookDto> recommendedBooks = recommendationService.getRecommendationsWithCache(childProfileId); // Redis에서 먼저 조회하고 없으면 DB에서 가져와 Redis에 저장
        List<RecommendBookDto> popularBooks = recommendationService.getPopularRecommendations(); // 좋아요 순 인기도서 5

        Map<String, List<RecommendBookDto>> finalBooks = new HashMap<>();
        finalBooks.put("recommendedBooks", recommendedBooks);
        finalBooks.put("popularBooks", popularBooks);

        return ApiUtil.success(finalBooks);
    }
}
