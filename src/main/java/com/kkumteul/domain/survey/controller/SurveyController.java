package com.kkumteul.domain.survey.controller;

import com.kkumteul.domain.survey.dto.SurveyResultDto;
import com.kkumteul.domain.survey.dto.SurveyResultRequestDto;
import com.kkumteul.domain.survey.service.pattern.SurveyFacade;
import com.kkumteul.util.ApiUtil;
import com.kkumteul.util.ApiUtil.ApiSuccess;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/survey")
public class SurveyController {

    private final SurveyFacade surveyFacade;

    @PostMapping
    public ApiSuccess<?> submitSurvey(
            @RequestBody SurveyResultRequestDto surveyResultRequestDto,
            HttpSession session) {
        //childProfileId 가져오는 방식 구현 필요.
        Long childProfileId = 1L;

        surveyFacade.submitSurvey(surveyResultRequestDto, childProfileId);

        return ApiUtil.success("설문 결과가 성공적으로 저장되었습니다.");
    }

    @GetMapping("/result")
    public ApiSuccess<SurveyResultDto> getSurveyResult() {
        Long childProfileId = 1L;

        SurveyResultDto resultDto = surveyFacade.getSurveyResult(childProfileId);

        return ApiUtil.success(resultDto);
    }
}
