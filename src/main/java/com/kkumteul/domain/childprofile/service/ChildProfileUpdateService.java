package com.kkumteul.domain.childprofile.service;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.history.entity.HistoryCreatedType;
import com.kkumteul.domain.history.entity.MBTIScore;
import com.kkumteul.domain.history.service.ChildPersonalityHistoryService;
import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.mbti.service.MBTIService;
import com.kkumteul.dto.ScoreUpdateEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChildProfileUpdateService {

    private final PersonalityScoreService personalityScoreService;
    private final ChildProfileService childProfileService;
    private final ChildPersonalityHistoryService historyService;
    private final MBTIService mbtiService;

    @Transactional
    public void updateScoresAndHistory(Long childProfileId, ScoreUpdateEventDto aggregatedEvent) {
        personalityScoreService.bulkUpdateScores(childProfileId, aggregatedEvent);

        ChildProfile childProfile = childProfileService.getChildProfileWithMBTIScore(childProfileId);
        createAndUpdateHistory(childProfile);
    }

    private void createAndUpdateHistory(ChildProfile childProfile) {
        MBTIScore currentMBTIScore = MBTIScore.fromCumulativeScore(childProfile.getCumulativeMBTIScore());
        MBTI mbti = mbtiService.getMBTI(mbtiService.checkMBTIType(currentMBTIScore));
        currentMBTIScore.setMbti(mbti);

        ChildPersonalityHistory history = historyService.createHistory(childProfile.getId(), currentMBTIScore,
                HistoryCreatedType.FEEDBACK);

        historyService.updatePreferredGenresByScore(history, childProfile.getGenreScores());
        historyService.updatePreferredTopicsByScore(history, childProfile.getTopicScores());
        log.info("Create History - ChildProfile ID: {}", childProfile.getId());
    }
}
