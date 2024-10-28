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

    // 총 MBTI 정보(ENFJ 등)
    private MBTIName mbti;

    // 세부 MBTI 점수
    private double IScore;
    private double EScore;
    private double NScore;
    private double SScore;
    private double FScore;
    private double TScore;
    private double PScore;
    private double JScore;

    // 가중치 점수
    private double score;

    @Builder
    public ChildDataDto(Long id, Gender gender, Date birthDate, List<TopicDto> topics, List<GenreDto> genres, MBTIName mbti, double IScore, double EScore, double NScore, double SScore, double FScore, double TScore, double PScore, double JScore) {
        this.id = id;
        this.gender = gender;
        this.birthDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();  // Date를 LocalDate로 변환;
        this.topics = topics;
        this.genres = genres;
        this.mbti = mbti;
        this.IScore = IScore;
        this.EScore = EScore;
        this.NScore = NScore;
        this.SScore = SScore;
        this.FScore = FScore;
        this.TScore = TScore;
        this.PScore = PScore;
        this.JScore = JScore;
    }

    public void addScore(double newScore) {
        this.score += newScore;
    }
}
