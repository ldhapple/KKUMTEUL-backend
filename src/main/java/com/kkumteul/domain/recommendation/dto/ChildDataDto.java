package com.kkumteul.domain.recommendation.dto;

import com.kkumteul.domain.childprofile.entity.Gender;
import com.kkumteul.domain.mbti.entity.MBTIName;
import lombok.*;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChildDataDto {

    private Long id;
    private Gender gender;
    private LocalDate birthDate;
    private List<TopicDto> topics = new ArrayList<>();
    private List<GenreDto> genres = new ArrayList<>();
    private MBTIName mbti;
    private double score;

    @Builder
    public ChildDataDto(Long id, Gender gender, Date birthDate, MBTIName mbti, List<TopicDto> topics, List<GenreDto> genres) {
        this.id = id;
        this.gender = gender;
        this.birthDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();  // Date를 LocalDate로 변환
        this.mbti = mbti;
        this.topics = topics;
        this.genres = genres;
    }

    public void addScore(double newScore) {
        this.score += newScore;
    }
}
