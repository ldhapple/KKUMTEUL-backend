package com.kkumteul.domain.survey.service.pattern;

import com.kkumteul.domain.survey.dto.SurveyResultDto;
import com.kkumteul.domain.survey.dto.SurveyResultRequestDto;

public interface SurveyFacade {
    void submitSurvey(SurveyResultRequestDto requestDto, Long childProfileId);
    SurveyResultDto getSurveyResult(Long childProfileId);
}
