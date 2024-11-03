package com.kkumteul.domain.recommendation.filter;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.repository.BookLikeRepository;
import com.kkumteul.domain.recommendation.dto.BookDataDto;
import com.kkumteul.domain.recommendation.dto.ChildDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class CollaborativeFilter {
    private final SimilarityCalculator similarityCalculator;

    // 유사한 자녀 프로필 찾기 메서드
    public List<ChildDataDto> findSimilarProfiles(ChildDataDto targetProfile, List<ChildDataDto> allProfiles) {
        List<ChildDataDto> similarProfiles = new ArrayList<>();

        // 모든 프로필과 비교해 유사도 계산
        for (ChildDataDto profile : allProfiles) {
            if (!profile.getId().equals(targetProfile.getId())) { // 본인 제외
                double similarity = similarityCalculator.calculateSimilarity(targetProfile, profile);
                if (similarity > 0) { // 유사도 임계값 0.1 이상인 경우만 추가
//                    log.info("유사 사용자 ID " +profile.getId() + "의 프로필 유사도 : " + similarity);
                    profile.addScore(similarity); // 유사도 점수 누적
                    similarProfiles.add(profile); // 유사한 프로필 목록에 추가
                }
            }
        }
        return similarProfiles;
    }
}

