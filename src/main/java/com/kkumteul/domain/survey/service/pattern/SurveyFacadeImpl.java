package com.kkumteul.domain.survey.service.pattern;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.service.ChildProfileService;
import com.kkumteul.domain.childprofile.service.PersonalityScoreService;
import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.history.entity.HistoryCreatedType;
import com.kkumteul.domain.history.entity.MBTIScore;
import com.kkumteul.domain.history.service.ChildPersonalityHistoryService;
import com.kkumteul.domain.mbti.dto.MBTIPercentageDto;
import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.mbti.service.MBTIService;
import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.entity.Topic;
import com.kkumteul.domain.survey.dto.FavoriteDto;
import com.kkumteul.domain.survey.dto.MbtiDto;
import com.kkumteul.domain.survey.dto.SurveyResultDto;
import com.kkumteul.domain.survey.dto.SurveyResultRequestDto;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyFacadeImpl implements SurveyFacade {

    private final MBTIService mbtiService;
    private final ChildPersonalityHistoryService historyService;
    private final ChildProfileService childProfileService;
    private final PersonalityScoreService personalityScoreService;

    @Override
    @Transactional
    public SurveyResultDto submitSurvey(SurveyResultRequestDto requestDto, Long childProfileId) {
        log.info("Submit survey Input childProfileId: {}", childProfileId);

        historyService.deleteDiagnosisHistory(childProfileId);

        ChildProfile childProfile = childProfileService.getChildProfile(childProfileId);
        /*
        진단 시 누적 점수는 초기화된다.
        설문 답변을 통해 MBTI 점수를 계산한다.
        기존 누적 MBTI 점수를 초기화하고 계산한 MBTI 점수를 누적 MBTI 점수에 update 한다.

        설문 답변을 통해 장르/주제어 누적 점수를 update한다.
        누적 점수를 바탕으로 선호 장르/주제어를 저장한다.
         */
        MBTIScore mbtiScore = mbtiService.calculateMBTIScore(requestDto.getAnswers());
//        personalityScoreService.resetCumulativeMBTIScore(childProfileId);
        personalityScoreService.updateCumulativeMBTIScore(childProfileId, mbtiScore);

        personalityScoreService.resetFavoriteScores(childProfileId);
        personalityScoreService.updateFavoriteGenresScore(childProfile, requestDto.getFavoriteGenres());
        personalityScoreService.updateFavoriteTopicsScore(childProfile, requestDto.getFavoriteTopics());

        ChildPersonalityHistory latestHistory = historyService.createHistory(childProfileId, mbtiScore,
                HistoryCreatedType.DIAGNOSIS);

        List<Genre> preferredGenres = historyService.updatePreferredGenresByScore(latestHistory, childProfile.getGenreScores());
        List<Topic> preferredTopics = historyService.updatePreferredTopicsByScore(latestHistory, childProfile.getTopicScores());

        return getSurveyResult(childProfile, mbtiScore, mbtiScore.getMbti(), preferredGenres, preferredTopics);
    }

    @Override
    @Transactional
    public void reSurvey(Long childProfileId) {
        log.info("Resurvey - Delete DiagnosisHistory ChildProfile ID: {}", childProfileId);
        historyService.deleteDiagnosisHistory(childProfileId); //필요
    }

    private SurveyResultDto getSurveyResult(ChildProfile childProfile, MBTIScore mbtiScore, MBTI mbti, List<Genre> preferredGenres, List<Topic> preferredTopics) {
        MBTIPercentageDto mbtiResult = mbtiService.calculatePercentages(mbtiScore);

        List<FavoriteDto> favoriteGenresDto = preferredGenres.stream()
                .map(genre -> new FavoriteDto(genre.getName(), genre.getImage()))
                .toList();

        List<FavoriteDto> favoriteTopicsDto = preferredTopics.stream()
                .map(topic -> new FavoriteDto(topic.getName(), topic.getImage()))
                .toList();

        return SurveyResultDto.builder()
                .IPercent(mbtiResult.getIPercent())
                .EPercent(mbtiResult.getEPercent())
                .SPercent(mbtiResult.getSPercent())
                .NPercent(mbtiResult.getNPercent())
                .TPercent(mbtiResult.getTPercent())
                .FPercent(mbtiResult.getFPercent())
                .JPercent(mbtiResult.getJPercent())
                .PPercent(mbtiResult.getPPercent())
                .mbtiResult(MbtiDto.fromEntity(mbti))
                .favoriteGenres(favoriteGenresDto)
                .favoriteTopics(favoriteTopicsDto)
                .profileImage(childProfile.getProfileImage())
                .childName(childProfile.getName())
                .childBirthDate(childProfile.getBirthDate())
                .diagnosisDate(LocalDateTime.now())
                .build();
    }
}
