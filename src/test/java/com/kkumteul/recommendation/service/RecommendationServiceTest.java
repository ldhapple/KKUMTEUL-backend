package com.kkumteul.recommendation.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.repository.BookRepository;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import com.kkumteul.domain.recommendation.entity.Recommendation;
import com.kkumteul.domain.recommendation.repository.RecommendationRepository;
import com.kkumteul.domain.recommendation.dto.RecommendBookDto;
import com.kkumteul.domain.recommendation.service.RecommendationService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.Optional;
import java.util.List;

import static com.kkumteul.domain.childprofile.entity.Gender.MALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")  // 테스트용 프로파일 사용
@TestExecutionListeners(
        listeners = TransactionalTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@Transactional
class RecommendationServiceTest {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ChildProfileRepository childProfileRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    private static final Long CHILD_PROFILE_ID = 1L;

    @BeforeEach
    void setUp() {
        // 트랜잭션에 강제로 참여
        entityManager.joinTransaction();

        // 테스트 데이터 설정 및 DB 삽입
        Book book1 = new Book("Title1", "Book1", "Author1", "Description1", null, null, null, null, null, null, null);
        Book book2 = new Book("Title2", "Book2", "Author2", "Description2", null, null, null, null, null, null, null);

        // Book 엔티티 먼저 저장
        book1 = bookRepository.save(book1);
        book2 = bookRepository.save(book2);

        ChildProfile childProfile = new ChildProfile("홍길동", MALE, null, null, null);
        childProfile = childProfileRepository.save(childProfile);

        Recommendation recommendation1 = new Recommendation(book1, childProfile);
        Recommendation recommendation2 = new Recommendation(book2, childProfile);

        // Recommendation 엔티티 저장 및 flush
        recommendationRepository.saveAll(List.of(recommendation1, recommendation2));
        recommendationRepository.flush();

        // Redis 캐시에 데이터를 JSON 문자열로 저장
        try {
            List<RecommendBookDto> mockBooks = List.of(
                    new RecommendBookDto(1L, "Title1", null),
                    new RecommendBookDto(2L, "Title2", null)
            );
            String jsonMockBooks = objectMapper.writeValueAsString(mockBooks);
            redisTemplate.opsForValue().set("recommendations::" + CHILD_PROFILE_ID, jsonMockBooks);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    @Test
    @DisplayName("캐싱 성능 비교")
    void testCachingPerformance() {
        // Redis 캐시 조회 성능 측정
        long redisStartTime = System.nanoTime();
        String cachedJsonBooks = (String) redisTemplate.opsForValue().get("recommendations::" + CHILD_PROFILE_ID);
        List<RecommendBookDto> redisBooks = null;
        try {
            redisBooks = objectMapper.readValue(cachedJsonBooks, new TypeReference<List<RecommendBookDto>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        long redisEndTime = System.nanoTime();
        long redisDurationInMillis = (redisEndTime - redisStartTime) / 1_000_000;


        assertEquals(2, redisBooks.size());

        // Redis 캐시 삭제 후 DB 조회 성능 측정
        redisTemplate.delete("recommendations::" + CHILD_PROFILE_ID);

        long dbStartTime = System.nanoTime();
        List<RecommendBookDto> dbBooks = recommendationService.getRecommendedBooks(CHILD_PROFILE_ID);
        long dbEndTime = System.nanoTime();
        long dbDurationInMillis = (dbEndTime - dbStartTime) / 1_000_000;

        System.out.println("Redis 캐싱 조회 실행 시간: " + redisDurationInMillis + "ms");
        System.out.println("DB 조회 실행 시간: " + dbDurationInMillis + "ms");

        // 검증: 반환된 리스트의 크기만 확인
        assertEquals(2, dbBooks.size());
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


}