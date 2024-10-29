package com.kkumteul.recommendation.service;


import com.kkumteul.domain.recommendation.dto.BookDataDto;
import com.kkumteul.domain.recommendation.dto.RecommendBookDto;
import com.kkumteul.domain.recommendation.service.RecommendationService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional  // 테스트 후 데이터 롤백
public class RecommendationServiceTest {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final Long CHILD_PROFILE_ID = 1L;

    @BeforeEach
    void clearCache() {
        // 캐시 초기화: 성능 비교를 위한 데이터 일관성 확보
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

//    @Test
//    @DisplayName("콘텐츠 기반 필터링")
//    void testGetRecommendations() {
//
//        // 서비스 호출
//        List<BookDataDto> recommendations = recommendationService.getRecommendations(1L);
//
//        // 검증
//        assertThat(recommendations).hasSize(2);
//        assertThat(recommendations.get(0).getTitle()).isEqualTo("제목1");
//        assertThat(recommendations.get(1).getTitle()).isEqualTo("제목2");
//    }
//
//    @Test
//    @DisplayName("가중치를 사용한 콘텐츠 기반+협업 필터링")
//    void testGetRecommendations2() {
//
//        // 서비스 호출
//        List<BookDataDto> recommendations = recommendationService.getRecommendations(1L);
//
//        System.out.println("추천된 책 목록:");
//        recommendations.forEach(book ->
//                System.out.println("아이디: " + book.getBookId() + ", 제목: " + book.getTitle() + ",  점수: " + book.getScore())
//        );
//
//        // 검증
////        assertThat(recommendations).hasSize(2);
////        assertThat(recommendations.get(0).getTitle()).isEqualTo("제목1");
////        assertThat(recommendations.get(1).getTitle()).isEqualTo("제목2");
//    }

    @Test
    @DisplayName("캐싱 성능 비교")
    void test(){
        // 초기화
        recommendationService.getRecommendationsWithCache(CHILD_PROFILE_ID);


        long startTime = System.nanoTime();  // 시작 시간 측정

        List<RecommendBookDto> redisBooks = recommendationService.getRecommendationsWithCache(1L); // Redis

        long endTime = System.nanoTime();  // 끝 시간 측정

        long startTime2 = System.nanoTime();

        List<RecommendBookDto> dbBooks =recommendationService.getRecommendedBooks(1L); // DB

        long endTime2 = System.nanoTime();

        // 실행 시간 계산 (밀리초로 변환)
        long redisDurationInMillis = (endTime - startTime) / 1_000_000;
        long dbDurationInMillis = (endTime2 - startTime2) / 1_000_000;

        System.out.println("Redis 캐싱 조회 실행 시간: " + redisDurationInMillis + "ms | " + "DB 조회 실행 시간: " + dbDurationInMillis + "ms");
    }
}