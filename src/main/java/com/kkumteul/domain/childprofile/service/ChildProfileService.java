package com.kkumteul.domain.childprofile.service;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.CumulativeMBTIScore;
import com.kkumteul.domain.childprofile.entity.Gender;
import com.kkumteul.domain.childprofile.entity.GenreScore;
import com.kkumteul.domain.childprofile.entity.TopicScore;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import com.kkumteul.domain.childprofile.repository.CumulativeMBTIScoreRepository;
import com.kkumteul.domain.childprofile.repository.GenreScoreRepository;
import com.kkumteul.domain.childprofile.repository.TopicScoreRepository;
import com.kkumteul.domain.history.entity.MBTIScore;
import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.entity.Topic;
import com.kkumteul.domain.personality.repository.GenreRepository;
import com.kkumteul.domain.personality.repository.TopicRepository;
import com.kkumteul.domain.user.entity.User;
import com.kkumteul.exception.ChildProfileNotFoundException;
import java.util.Date;
import com.kkumteul.domain.childprofile.dto.ChildProfileDto;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import com.kkumteul.exception.ChildProfileNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChildProfileService {

    private final ChildProfileRepository childProfileRepository;
    private final CumulativeMBTIScoreRepository cumulativeMBTIScoreRepository;
    private final GenreScoreRepository genreScoreRepository;
    private final TopicScoreRepository topicScoreRepository;
    private final GenreRepository genreRepository;
    private final TopicRepository topicRepository;

    public ChildProfile createChildProfile(String name, Gender gender, Date birthDate, byte[] profileImage, User user) {
        //입력 매개변수 DTO로 수정
        ChildProfile childProfile = ChildProfile.builder()
                .name(name)
                .gender(gender)
                .birthDate(birthDate)
                .profileImage(profileImage)
                .user(user)
                .build();

        CumulativeMBTIScore cumulativeMBTIScore = CumulativeMBTIScore.builder()
                .iScore(0.0)
                .eScore(0.0)
                .sScore(0.0)
                .nScore(0.0)
                .tScore(0.0)
                .fScore(0.0)
                .jScore(0.0)
                .pScore(0.0)
                .build();

        childProfile.setCumulativeMBTIScore(cumulativeMBTIScore);

        List<Genre> allGenres = genreRepository.findAll();
        for (Genre genre : allGenres) {
            GenreScore genreScore = GenreScore.builder()
                    .genre(genre)
                    .score(0.0)
                    .build();
            childProfile.addGenreScore(genreScore);
        }

        List<Topic> allTopics = topicRepository.findAll();
        for (Topic topic : allTopics) {
            TopicScore topicScore = TopicScore.builder()
                    .topic(topic)
                    .score(0.0)
                    .build();
            childProfile.addTopicScore(topicScore);
        }

        log.info("create ChildProfile UserId: {}", user.getId());
        return childProfileRepository.save(childProfile);
    }

    public void resetCumulativeMBTIScore(Long childProfileId) {
        log.info("reset CumulativeMBTIScore ChildProfile ID: {}", childProfileId);
        CumulativeMBTIScore cumulativeScore = cumulativeMBTIScoreRepository.findByChildProfileId(childProfileId)
                .orElseThrow(() -> new IllegalArgumentException("점수가 존재하지 않습니다."));

        cumulativeScore.resetScores();
    }

    public void resetFavoriteScores(Long childProfileId) {
        log.info("reset Genre/Topic Score ChildProfile ID: {}", childProfileId);
        List<TopicScore> topicScores = topicScoreRepository.findByChildProfileId(childProfileId);
        List<GenreScore> genreScores = genreScoreRepository.findByChildProfileId(childProfileId);

        for (TopicScore topicScore : topicScores) {
            topicScore.resetScore();
        }

        for (GenreScore genreScore : genreScores) {
            genreScore.resetScore();
        }
    }

    public CumulativeMBTIScore updateCumulativeMBTIScore(Long childProfileId, MBTIScore mbtiScore) {
        log.info("Update CumulativeMBTIScore ChildProfile ID: {}", childProfileId);
        CumulativeMBTIScore cumulativeScore = cumulativeMBTIScoreRepository.findByChildProfileId(childProfileId)
                .orElseThrow(() -> new IllegalArgumentException("점수가 존재하지 않습니다."));

        return cumulativeScore.updateScores(mbtiScore);
    }

    @Transactional(readOnly = true)
    public ChildProfile getChildProfile(Long childProfileId) {
        log.info("get ChildProfile InputId: {}", childProfileId);
        return childProfileRepository.findById(childProfileId)
                .orElseThrow(() -> new ChildProfileNotFoundException(childProfileId));

    public List<ChildProfileDto> getChildProfileList(Long userId) {
        log.info("getChildProfiles - Input userId: {}", userId);
        List<ChildProfile> childProfiles = childProfileRepository.findByUserId(userId)
                .filter(profiles -> !profiles.isEmpty())
                .orElseThrow(() -> new ChildProfileNotFoundException(userId));

        log.info("found childProfiles: {}", childProfiles.size());
        return childProfiles.stream()
                .map(ChildProfileDto::fromEntity)
                .toList();
    }

    public void validateChildProfile(Long childProfileId) {
        log.info("validate exist childProfile: {}", childProfileId);
        childProfileRepository.findById(childProfileId).orElseThrow(
                () -> new IllegalArgumentException("childProfile not found - childProfileId : " + childProfileId));

    }
}
