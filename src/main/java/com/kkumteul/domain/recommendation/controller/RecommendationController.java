package com.kkumteul.domain.recommendation.controller;

import com.kkumteul.domain.recommendation.dto.RecommendBookDto;
import com.kkumteul.domain.recommendation.service.RecommendationService;
import com.kkumteul.util.ApiUtil;
import com.kkumteul.util.ApiUtil.ApiSuccess;
import java.util.List;
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

        List<RecommendBookDto> recommendedBooks = recommendationService.getRecommendedBooks(childProfileId);

        return ApiUtil.success(recommendedBooks);
    }
}
