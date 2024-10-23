package com.kkumteul.domain.survey.service.pattern;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.GenreScore;
import com.kkumteul.domain.childprofile.entity.TopicScore;
import com.kkumteul.domain.childprofile.service.ChildProfileService;
import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.history.entity.FavoriteGenre;
import com.kkumteul.domain.history.entity.FavoriteTopic;
import com.kkumteul.domain.history.entity.HistoryCreatedType;
import com.kkumteul.domain.history.entity.MBTIScore;
import com.kkumteul.domain.history.service.ChildPersonalityHistoryService;
import com.kkumteul.domain.childprofile.entity.CumulativeMBTIScore;
import com.kkumteul.domain.mbti.dto.MBTIPercentageDto;
import com.kkumteul.domain.mbti.service.MBTIService;
import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.entity.Topic;
import com.kkumteul.domain.survey.dto.FavoriteDto;
import com.kkumteul.domain.survey.dto.MbtiDto;
import com.kkumteul.domain.survey.dto.SurveyResultDto;
import com.kkumteul.domain.survey.dto.SurveyResultRequestDto;
import com.kkumteul.exception.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SurveyFacadeImpl implements SurveyFacade {

    private final MBTIService mbtiService;
    private final ChildPersonalityHistoryService historyService;
    private final ChildProfileService childProfileService;

    @Override
    public void submitSurvey(SurveyResultRequestDto requestDto, Long childProfileId) {
        log.info("Submit survey Input childProfileId: {}", childProfileId);

        ChildProfile childProfile = childProfileService.getChildProfile(childProfileId);
        /*
        진단 시 누적 점수는 초기화된다.
        설문 답변을 통해 MBTI 점수를 계산한다.
        기존 누적 MBTI 점수를 초기화하고 계산한 MBTI 점수를 누적 MBTI 점수에 update 한다.
         */
        MBTIScore mbtiScore = mbtiService.calculateMBTIScore(requestDto.getAnswers());
        childProfileService.resetCumulativeMBTIScore(childProfileId);
        childProfileService.updateCumulativeMBTIScore(childProfileId, mbtiScore);

        historyService.deleteDiagnosisHistory(childProfileId);
        historyService.createHistory(childProfileId, mbtiScore,HistoryCreatedType.DIAGNOSIS);

        /*
        진단 시 누적 점수는 초기화된다.
        설문 답변을 통해 장르/주제어 누적 점수를 update한다.
        누적 점수를 바탕으로 선호 장르/주제어를 저장한다.
         */
        childProfileService.resetFavoriteScores(childProfileId);
        updateFavoriteGenres(childProfile, requestDto.getFavoriteGenres());
        updateFavoriteTopics(childProfile, requestDto.getFavoriteTopics());
    }

    @Override
    public SurveyResultDto getSurveyResult(Long childProfileId) {
        log.info("Get survey result ChildProfile ID: {}", childProfileId);

        ChildProfile childProfile = childProfileService.getChildProfile(childProfileId);

        ChildPersonalityHistory latestHistory = historyService.getLatestHistory(childProfileId);

        MBTIPercentageDto mbtiResult = mbtiService.calculatePercentages(latestHistory.getMbtiScore());

        // 트랜잭션으로 인해 점수를 update하는 부분과 분리
        List<Genre> preferredGenres = determinePreferredGenres(childProfile.getGenreScores());
        List<Topic> preferredTopics = determinePreferredTopics(childProfile.getTopicScores());

        for (Genre genre : preferredGenres) {
            FavoriteGenre favoriteGenre = FavoriteGenre.builder()
                    .genre(genre)
                    .build();
            latestHistory.addFavoriteGenre(favoriteGenre);
        }

        for (Topic topic : preferredTopics) {
            FavoriteTopic favoriteTopic = FavoriteTopic.builder()
                    .topic(topic)
                    .build();
            latestHistory.addFavoriteTopic(favoriteTopic);
        }

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
                .mbtiResult(MbtiDto.fromEntity(latestHistory.getMbtiScore().getMbti()))
                .favoriteGenres(favoriteGenresDto)
                .favoriteTopics(favoriteTopicsDto)
                .build();
    }

    @Override
    public void reSurvey(Long childProfileId) {
        log.info("Resurvey - Delete DiagnosisHistory ChildProfile ID: {}", childProfileId);
        historyService.deleteDiagnosisHistory(childProfileId);
    }

    /*
        선호 장르/주제어 관련 로직의 위치가 여기가 맞는지 고민 필요.
         */
    private void updateFavoriteGenres(ChildProfile childProfile, List<Long> favoriteGenreIds) {
        log.info("Updating GenreScores ChildProfile ID: {}", childProfile.getId());

        // 선택한 장르에 5점 부여
        for (Long genreId : favoriteGenreIds) {
            GenreScore genreScore = childProfile.getGenreScores().stream()
                    .filter(gs -> gs.getGenre().getId().equals(genreId))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException(genreId));
            genreScore.updateScore(5.0);
        }

        log.info("GenreScores updated ChildProfile ID: {}", childProfile.getId());
    }

    private void updateFavoriteTopics(ChildProfile childProfile, List<Long> favoriteTopicIds) {
        log.info("Updating TopicScores ChildProfile ID: {}", childProfile.getId());

        // 선택한 주제어에 5점 부여
        for (Long topicId : favoriteTopicIds) {
            TopicScore topicScore = childProfile.getTopicScores().stream()
                    .filter(ts -> ts.getTopic().getId().equals(topicId))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException(topicId));
            topicScore.updateScore(5.0);
        }

        log.info("TopicScores updated ChildProfile ID: {}", childProfile.getId());
    }

    private List<Genre> determinePreferredGenres(List<GenreScore> genreScores) {
        log.info("set preferred genres");

        double averageScore = genreScores.stream()
                .mapToDouble(GenreScore::getScore)
                .average()
                .orElse(0.0);

        log.debug("Average GenreScore: {}", averageScore);

        List<Genre> preferredGenres = genreScores.stream()
                .filter(gs -> gs.getScore() > averageScore)
                .map(GenreScore::getGenre)
                .toList();

        return preferredGenres;
    }

    private List<Topic> determinePreferredTopics(List<TopicScore> topicScores) {
        log.info("set preferred topics");

        double averageScore = topicScores.stream()
                .mapToDouble(TopicScore::getScore)
                .average()
                .orElse(0.0);

        log.debug("Average TopicScore: {}", averageScore);

        List<Topic> preferredTopics = topicScores.stream()
                .filter(ts -> ts.getScore() > averageScore)
                .map(TopicScore::getTopic)
                .toList();

        return preferredTopics;
    }
}
