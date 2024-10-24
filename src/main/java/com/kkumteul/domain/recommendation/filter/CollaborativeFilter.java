package com.kkumteul.domain.recommendation.filter;

import com.kkumteul.domain.recommendation.dto.ChildDataDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CollaborativeFilter {


    // 유사한 자녀 프로필 찾기 메서드
    public List<ChildDataDto> findSimilarProfiles(ChildDataDto targetProfile, List<ChildDataDto> allProfiles) {
        List<ChildDataDto> similarProfiles = new ArrayList<>();
        SimilarityCalculator similarityCalculator = new SimilarityCalculator();

        for (ChildDataDto profile : allProfiles) {
            if (profile.getId().equals(targetProfile.getId())) {
                continue;  // 자기 자신은 건너뜀
            }

            double similarity = similarityCalculator.calculateSimilarity(targetProfile, profile);
            if (similarity >= 0.1) {
                profile.addScore(similarity); // 유사도 점수 누적
                similarProfiles.add(profile); // 유사한 프로필에 넣기
            }
        }

        return similarProfiles;
    }

}

