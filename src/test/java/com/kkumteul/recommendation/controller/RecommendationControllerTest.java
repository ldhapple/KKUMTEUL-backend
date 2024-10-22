package com.kkumteul.recommendation.controller;

import com.kkumteul.domain.recommendation.service.RecommendationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RecommendationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;

    @Test
    @DisplayName("추천에 사용 될 데이터 가져오기")
    void testData() throws Exception{
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/recommend/1"))
                .andExpect(status().isOk())
                .andReturn();
        // 응답 본문을 문자열로 추출
        String content = result.getResponse().getContentAsString();

        // 응답 본문 출력
        System.out.println("Response: " + content);
    }
}
