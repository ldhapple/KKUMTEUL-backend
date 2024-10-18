package com.kkumteul.domain.survey.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MBTIAnswer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String option;
    private int effectScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private MBTIQuestion question;

    @Builder
    public MBTIAnswer(String option, int effectScore, MBTIQuestion question) {
        this.option = option;
        this.effectScore = effectScore;
        this.question = question;
    }
}
