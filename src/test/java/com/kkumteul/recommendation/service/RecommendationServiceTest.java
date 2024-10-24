package com.kkumteul.recommendation.service;


import com.kkumteul.domain.recommendation.dto.BookDataDto;
import com.kkumteul.domain.recommendation.service.RecommendationService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeAll;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional  // 테스트 후 데이터 롤백
public class RecommendationServiceTest {

    @Autowired
    private RecommendationService recommendationService;
    @Test
    @DisplayName("콘텐츠 기반 필터링")
    void testGetRecommendations() {

        // 서비스 호출
        List<BookDataDto> recommendations = recommendationService.getRecommendations(1L);

        // 검증
        assertThat(recommendations).hasSize(2);
        assertThat(recommendations.get(0).getTitle()).isEqualTo("제목1");
        assertThat(recommendations.get(1).getTitle()).isEqualTo("제목2");
    }

    @Test
    @DisplayName("가중치를 사용한 콘텐츠 기반+협업 필터링")
    void testGetRecommendations2() {

        // 서비스 호출
        List<BookDataDto> recommendations = recommendationService.getRecommendations(1L);

        System.out.println("추천된 책 목록:");
        recommendations.forEach(book ->
                System.out.println("아이디: " + book.getBookId() + ", 제목: " + book.getTitle() + ",  점수: " + book.getScore())
        );

        // 검증
//        assertThat(recommendations).hasSize(2);
//        assertThat(recommendations.get(0).getTitle()).isEqualTo("제목1");
//        assertThat(recommendations.get(1).getTitle()).isEqualTo("제목2");
    }
}