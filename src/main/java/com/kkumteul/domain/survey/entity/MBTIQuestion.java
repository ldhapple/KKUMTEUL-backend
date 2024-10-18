package com.kkumteul.domain.survey.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MBTIQuestion {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    @Enumerated(EnumType.STRING)
    private MBTIEffect mbtiEffect;

    public MBTIQuestion(String question, MBTIEffect mbtiEffect) {
        this.question = question;
        this.mbtiEffect = mbtiEffect;
    }
}
