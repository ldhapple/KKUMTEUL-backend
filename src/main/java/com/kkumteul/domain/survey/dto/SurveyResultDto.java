package com.kkumteul.domain.survey.dto;

import java.time.LocalDateTime;
import java.util.Date;
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
    private String childName;
    private Date childBirthDate;
    private LocalDateTime diagnosisDate;
}
