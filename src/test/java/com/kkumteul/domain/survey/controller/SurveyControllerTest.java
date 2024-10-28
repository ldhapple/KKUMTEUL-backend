package com.kkumteul.domain.survey.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kkumteul.domain.survey.dto.FavoriteDto;
import com.kkumteul.domain.survey.dto.MbtiDto;
import com.kkumteul.domain.survey.dto.SurveyResultDto;
import com.kkumteul.domain.survey.dto.SurveyResultRequestDto;
import com.kkumteul.domain.survey.service.pattern.SurveyFacade;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SurveyController.class)
class SurveyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SurveyFacade surveyFacade;

    @Test
    @DisplayName("설문 제출 테스트")
    void testSubmitSurvey() throws Exception {
        String requestBody = """
            {
                "answers": [
                    { "mbtiEffect": "I", "score": 3 },
                    { "mbtiEffect": "E", "score": 2 },
                    { "mbtiEffect": "S", "score": 4 },
                    { "mbtiEffect": "N", "score": 1 },
                    { "mbtiEffect": "T", "score": 5 },
                    { "mbtiEffect": "F", "score": 3 },
                    { "mbtiEffect": "J", "score": 4 },
                    { "mbtiEffect": "P", "score": 2 }
                ],
                "favoriteGenres": [1, 3],
                "favoriteTopics": [2, 4]
            }
        """;

        mockMvc.perform(post("/api/survey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.ipercent").value(60.0))
                .andExpect(jsonPath("$.response.epercent").value(40.0))
                .andExpect(jsonPath("$.response.spercent").value(70.0))
                .andExpect(jsonPath("$.response.npercent").value(30.0))
                .andExpect(jsonPath("$.response.tpercent").value(80.0))
                .andExpect(jsonPath("$.response.fpercent").value(20.0))
                .andExpect(jsonPath("$.response.jpercent").value(75.0))
                .andExpect(jsonPath("$.response.ppercent").value(25.0))
                .andExpect(jsonPath("$.response.mbtiResult.mbtiName").value("INTJ"))
                .andExpect(jsonPath("$.response.mbtiResult.mbtiTitle").value("수호자"))
                .andExpect(jsonPath("$.response.mbtiResult.mbtiDescription").value("상상력이 풍부한"))
                .andExpect(jsonPath("$.response.favoriteGenres[0].name").value("그림책"))
                .andExpect(jsonPath("$.response.favoriteGenres[1].name").value("옛날이야기"))
                .andExpect(jsonPath("$.response.favoriteTopics[0].name").value("식물"))
                .andExpect(jsonPath("$.response.favoriteTopics[1].name").value("나무"));
    }

    @Test
    @DisplayName("설문 결과 삭제 및 재진단 테스트")
    void testDeleteAndResurvey() throws Exception {
        mockMvc.perform(delete("/api/survey/result"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("기존 진단 결과가 성공적으로 삭제되었습니다."));
    }
}