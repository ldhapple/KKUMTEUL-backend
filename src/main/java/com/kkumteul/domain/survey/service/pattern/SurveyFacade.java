package com.kkumteul.domain.survey.service.pattern;

import com.kkumteul.domain.survey.dto.SurveyResultDto;
import com.kkumteul.domain.survey.dto.SurveyResultRequestDto;

public interface SurveyFacade {
    SurveyResultDto submitSurvey(SurveyResultRequestDto requestDto, Long childProfileId);
    void reSurvey(Long childProfileId);
}
