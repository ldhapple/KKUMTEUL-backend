package com.kkumteul.domain.childprofile.entity;

import com.kkumteul.domain.history.entity.MBTIScore;
import com.kkumteul.domain.mbti.entity.MBTI;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CumulativeMBTIScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double iScore;
    private double eScore;
    private double nScore;
    private double sScore;
    private double tScore;
    private double fScore;
    private double jScore;
    private double pScore;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_profile_id")
    private ChildProfile childProfile;

    @Builder
    public CumulativeMBTIScore(double iScore, double eScore, double nScore, double sScore, double tScore, double fScore,
                               double jScore,
                               double pScore, ChildProfile childProfile) {
        this.iScore = iScore;
        this.eScore = eScore;
        this.nScore = nScore;
        this.sScore = sScore;
        this.tScore = tScore;
        this.fScore = fScore;
        this.jScore = jScore;
        this.pScore = pScore;
        this.childProfile = childProfile;
    }

    public static CumulativeMBTIScore init() {
        return CumulativeMBTIScore.builder()
                .iScore(0.0)
                .eScore(0.0)
                .sScore(0.0)
                .nScore(0.0)
                .tScore(0.0)
                .fScore(0.0)
                .jScore(0.0)
                .pScore(0.0)
                .build();
    }

    public CumulativeMBTIScore updateScores(MBTIScore mbtiScore) {
        this.iScore += mbtiScore.getIScore();
        this.eScore += mbtiScore.getEScore();
        this.nScore += mbtiScore.getSScore();
        this.sScore += mbtiScore.getNScore();
        this.tScore += mbtiScore.getTScore();
        this.fScore += mbtiScore.getFScore();
        this.jScore += mbtiScore.getJScore();
        this.pScore += mbtiScore.getPScore();

        return this;
    }

    public void resetScores() {
        this.iScore = 0.0;
        this.eScore = 0.0;
        this.nScore = 0.0;
        this.sScore = 0.0;
        this.tScore = 0.0;
        this.fScore = 0.0;
        this.jScore = 0.0;
        this.pScore = 0.0;
    }

    public void setChildProfile(ChildProfile childProfile) {
        this.childProfile = childProfile;
    }
}
