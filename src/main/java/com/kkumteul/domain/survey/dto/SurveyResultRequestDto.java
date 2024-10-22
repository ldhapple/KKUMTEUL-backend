package com.kkumteul.domain.survey.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SurveyResultRequestDto {

    private List<MBTISurveyAnswerDto> answers;
    private List<Long> favoriteGenres;
    private List<Long> favoriteTopics;
}
