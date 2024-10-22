package com.kkumteul.domain.survey.dto;

import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.entity.Topic;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SurveyResultDto {

    private double IPercent;
    private double EPercent;
    private double SPercent;
    private double NPercent;
    private double TPercent;
    private double FPercent;
    private double JPercent;
    private double PPercent;
    private MbtiDto mbtiResult;
    private List<FavoriteDto> favoriteGenres;
    private List<FavoriteDto> favoriteTopics;
}
