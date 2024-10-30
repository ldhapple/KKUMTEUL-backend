package com.kkumteul.domain.survey.controller;

import com.kkumteul.domain.survey.dto.SurveyResultDto;
import com.kkumteul.domain.survey.dto.SurveyResultRequestDto;
import com.kkumteul.domain.survey.service.pattern.SurveyFacade;
import com.kkumteul.util.ApiUtil;
import com.kkumteul.util.ApiUtil.ApiSuccess;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/survey")
public class SurveyController {

    private final SurveyFacade surveyFacade;

    @PostMapping
    public ApiSuccess<?> submitSurvey(
            @RequestBody SurveyResultRequestDto surveyResultRequestDto) {

        SurveyResultDto resultDto = surveyFacade.submitSurvey(surveyResultRequestDto);

        return ApiUtil.success(resultDto);
    }

    @DeleteMapping
    public ApiSuccess<?> deleteAndResurvey(@RequestParam("childProfileId") Long childProfileId) {
        surveyFacade.reSurvey(childProfileId);

        return ApiUtil.success("기존 진단 결과가 성공적으로 삭제되었습니다.");
    }
}
