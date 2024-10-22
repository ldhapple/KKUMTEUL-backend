package com.kkumteul.domain.mbti.dto;

import com.kkumteul.domain.childprofile.entity.CumulativeMBTIScore;
import com.kkumteul.domain.history.entity.MBTIScore;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MBTIPercentageDto {

    private double iPercent;
    private double ePercent;
    private double sPercent;
    private double nPercent;
    private double tPercent;
    private double fPercent;
    private double jPercent;
    private double pPercent;

    public static MBTIPercentageDto calculatePercentage(MBTIScore mbtiScore) {
        return new MBTIPercentageDto(
                calculateMBTIField(mbtiScore.getIScore(), mbtiScore.getEScore()),
                calculateMBTIField(mbtiScore.getEScore(), mbtiScore.getIScore()),
                calculateMBTIField(mbtiScore.getSScore(), mbtiScore.getNScore()),
                calculateMBTIField(mbtiScore.getNScore(), mbtiScore.getSScore()),
                calculateMBTIField(mbtiScore.getTScore(), mbtiScore.getFScore()),
                calculateMBTIField(mbtiScore.getFScore(), mbtiScore.getTScore()),
                calculateMBTIField(mbtiScore.getJScore(), mbtiScore.getPScore()),
                calculateMBTIField(mbtiScore.getPScore(), mbtiScore.getJScore())
        );
    }

    private static double calculateMBTIField(double scoreA, double scoreB) {
        return scoreA / (scoreA + scoreB) * 100;
    }
}
