package com.kkumteul.domain.recommendation.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kkumteul.domain.recommendation.dto.RecommendBookDto;
import com.kkumteul.domain.recommendation.service.RecommendationService;
import com.kkumteul.exception.RecommendationBookNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;

    @Test
    @DisplayName("추천 도서 조회 API 성공 테스트")
    @WithMockUser("user1")
    void testGetRecommendedBooks() throws Exception {
        Long childProfileId = 1L;

        List<RecommendBookDto> recommendBooks = List.of(
                new RecommendBookDto(1L, "Title", new byte[]{})
        );
        List<RecommendBookDto> popularBooks = List.of(
                new RecommendBookDto(2L, "Popular", new byte[]{})
        );

        given(recommendationService.getPopularRecommendations()).willReturn(popularBooks);
        given(recommendationService.getRecommendedBooks(childProfileId)).willReturn(recommendBooks);

        mockMvc.perform(get("/api/recommendation/books")
                        .param("child", childProfileId.toString())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.recommendedBooks").isArray())
                .andExpect(jsonPath("$.response.recommendedBooks[0].bookTitle").value("Title"))
                .andExpect(jsonPath("$.response.popularBooks").isArray())
                .andExpect(jsonPath("$.response.popularBooks[0].bookTitle").value("Popular"));
    }

    @Test
    @WithMockUser("user")
    @DisplayName("추천 도서 조회 실패 테스트 - 프로필 ID가 잘못된 경우")
    void testGetRecommendedBooksNotFound() throws Exception {
        Long invalidProfileId = 999L;

        given(recommendationService.getRecommendedBooks(invalidProfileId))
                .willThrow(new RecommendationBookNotFoundException(invalidProfileId));

        mockMvc.perform(get("/api/recommendation/books")
                        .param("child", invalidProfileId.toString())
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
