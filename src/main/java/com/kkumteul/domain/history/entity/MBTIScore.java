package com.kkumteul.domain.history.entity;

import com.kkumteul.domain.childprofile.entity.CumulativeMBTIScore;
import com.kkumteul.domain.mbti.entity.MBTI;
import jakarta.persistence.CascadeType;
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
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MBTIScore {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double iScore;
    private double eScore;
    private double sScore;
    private double nScore;
    private double tScore;
    private double fScore;
    private double jScore;
    private double pScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mbti_id")
    private MBTI mbti;

    @Builder
    public MBTIScore(double iScore, double eScore, double sScore, double nScore, double tScore, double fScore,
                     double jScore, double pScore, MBTI mbti) {
        this.iScore = iScore;
        this.eScore = eScore;
        this.sScore = sScore;
        this.nScore = nScore;
        this.tScore = tScore;
        this.fScore = fScore;
        this.jScore = jScore;
        this.pScore = pScore;
        this.mbti = mbti;
    }

    public static MBTIScore fromCumulativeScore(CumulativeMBTIScore cumulativeMBTIScore) {


        return MBTIScore.builder()
                .iScore(cumulativeMBTIScore.getIScore())
                .eScore(cumulativeMBTIScore.getEScore())
                .sScore(cumulativeMBTIScore.getSScore())
                .nScore(cumulativeMBTIScore.getNScore())
                .tScore(cumulativeMBTIScore.getTScore())
                .fScore(cumulativeMBTIScore.getFScore())
                .jScore(cumulativeMBTIScore.getJScore())
                .pScore(cumulativeMBTIScore.getPScore())
                .build();
    }
}
