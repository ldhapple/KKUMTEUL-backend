package com.kkumteul.domain.recommendation.dto;

import com.kkumteul.domain.childprofile.entity.Gender;
import com.kkumteul.domain.mbti.entity.MBTIName;
import lombok.*;

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
    private Date birthDate;
    private List<TopicDto> topics = new ArrayList<>();
    private List<GenreDto> genres = new ArrayList<>();
    private MBTIName mbti;

    @Builder
    public ChildDataDto(Long id, Gender gender, Date birthDate, MBTIName mbti, List<TopicDto> topics, List<GenreDto> genres) {
        this.id = id;
        this.gender = gender;
        this.birthDate = birthDate;
        this.mbti = mbti;
        this.topics = topics;
        this.genres = genres;
    }
}
