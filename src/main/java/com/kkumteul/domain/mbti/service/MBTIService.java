package com.kkumteul.domain.mbti.service;

import com.kkumteul.domain.history.entity.MBTIScore;
import com.kkumteul.domain.mbti.dto.MBTIPercentageDto;
import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.childprofile.entity.CumulativeMBTIScore;
import com.kkumteul.domain.mbti.entity.MBTIName;
import com.kkumteul.domain.mbti.repository.MBTIRepository;
import com.kkumteul.domain.survey.dto.MBTISurveyAnswerDto;
import com.kkumteul.exception.InvalidMBTINameException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MBTIService {

    private final MBTIRepository mbtiRepository;

    public MBTIScore calculateMBTIScore(List<MBTISurveyAnswerDto> answers) {
        int iScore = 0;
        int eScore = 0;
        int sScore = 0;
        int nScore = 0;
        int tScore = 0;
        int fScore = 0;
        int pScore = 0;
        int jScore = 0;

        for (MBTISurveyAnswerDto answer : answers) {
            switch (answer.getMbtiEffect()) {
                case "I":
                    iScore += answer.getScore();
                    break;
                case "E":
                    eScore += answer.getScore();
                    break;
                case "S":
                    sScore += answer.getScore();
                    break;
                case "N":
                    nScore += answer.getScore();
                    break;
                case "T":
                    tScore += answer.getScore();
                    break;
                case "F":
                    fScore += answer.getScore();
                    break;
                case "J":
                    jScore += answer.getScore();
                    break;
                case "P":
                    pScore += answer.getScore();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid MBTI effect: " + answer.getMbtiEffect());
            }
        }

        log.debug("Calculated MBTI Scores - I: {}, E: {}, S: {}, N: {}, T: {}, F: {}, J: {}, P: {}",
                iScore, eScore, sScore, nScore, tScore, fScore, jScore, pScore);

        String mbtiType = checkMBTIType(iScore, eScore, sScore, nScore, tScore, fScore, jScore, pScore);
        log.debug("Determined MBTI Type: {}", mbtiType);

        MBTI mbti = getMBTI(mbtiType);
        log.debug("MBTI entity: {}", mbti);

        return MBTIScore.builder()
                .iScore(iScore)
                .eScore(eScore)
                .sScore(sScore)
                .nScore(nScore)
                .tScore(tScore)
                .fScore(fScore)
                .jScore(jScore)
                .pScore(pScore)
                .mbti(mbti)
                .build();
    }

    public String checkMBTIType(int iScore, int eScore, int sScore, int nScore, int tScore, int fScore, int jScore,
                                int pScore) {
        StringBuilder mbtiName = new StringBuilder();

        mbtiName.append(iScore >= eScore ? "I" : "E");
        mbtiName.append(nScore >= sScore ? "N" : "S");
        mbtiName.append(tScore >= fScore ? "T" : "F");
        mbtiName.append(jScore >= pScore ? "J" : "P");

        return mbtiName.toString();
    }

    public MBTI getMBTI(String mbtiName) {
        return mbtiRepository.findByMbti(MBTIName.fromString(mbtiName))
                .orElseThrow(() -> new InvalidMBTINameException(mbtiName));
    }

    public MBTIPercentageDto calculatePercentages(MBTIScore mbtiScore) {
        return MBTIPercentageDto.calculatePercentage(mbtiScore);
    }
}
