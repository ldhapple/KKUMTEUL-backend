package com.kkumteul.domain.mbti.entity;

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
public class MBTIScore {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int iScore;
    private int eScore;
    private int nScore;
    private int sScore;
    private int tScore;
    private int fScore;
    private int jScore;
    private int pScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mbti_id")
    private MBTI mbti;

    @Builder
    public MBTIScore(int iScore, int eScore, int nScore, int sScore, int tScore, int fScore, int jScore, int pScore,
                     MBTI mbti) {
        this.iScore = iScore;
        this.eScore = eScore;
        this.nScore = nScore;
        this.sScore = sScore;
        this.tScore = tScore;
        this.fScore = fScore;
        this.jScore = jScore;
        this.pScore = pScore;
        this.mbti = mbti;
    }
}
