package com.kkumteul.domain.survey.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kkumteul.domain.survey.dto.SurveyResultDto;
import com.kkumteul.domain.survey.service.pattern.SurveyFacade;
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
                .andExpect(jsonPath("$.response").value("설문 결과가 성공적으로 저장되었습니다."));
    }

    @Test
    @DisplayName("설문 결과 조회 테스트")
    void testGetSurveyResult() throws Exception {
        SurveyResultDto surveyResultDto = mock(SurveyResultDto.class);
//        given(surveyFacade.getSurveyResult(anyLong())).willReturn(surveyResultDto);

        mockMvc.perform(get("/api/survey/result"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("설문 결과 삭제 및 재진단 테스트")
    void testDeleteAndResurvey() throws Exception {
        mockMvc.perform(delete("/api/survey/result"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("기존 진단 결과가 성공적으로 삭제되었습니다."));
    }
}