package com.kkumteul.domain.childprofile.service;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookMBTI;
import com.kkumteul.domain.book.entity.BookTopic;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.CumulativeMBTIScore;
import com.kkumteul.domain.childprofile.entity.GenreScore;
import com.kkumteul.domain.childprofile.entity.TopicScore;
import com.kkumteul.domain.childprofile.repository.CumulativeMBTIScoreRepository;
import com.kkumteul.domain.childprofile.repository.GenreScoreRepository;
import com.kkumteul.domain.childprofile.repository.TopicScoreRepository;
import com.kkumteul.domain.history.entity.MBTIScore;
import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.entity.Topic;
import com.kkumteul.dto.ScoreUpdateEventDto;
import com.kkumteul.exception.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonalityScoreService {

    private final GenreScoreRepository genreScoreRepository;
    private final TopicScoreRepository topicScoreRepository;
    private final CumulativeMBTIScoreRepository cumulativeMBTIScoreRepository;

    @Transactional
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

    @Transactional
    public void updateFavoriteGenresScore(ChildProfile childProfile, List<Long> favoriteGenreIds) {
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

    @Transactional
    public void updateFavoriteTopicsScore(ChildProfile childProfile, List<Long> favoriteTopicIds) {
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

    @Transactional
    public void resetCumulativeMBTIScore(Long childProfileId) {
        log.info("reset CumulativeMBTIScore ChildProfile ID: {}", childProfileId);
        CumulativeMBTIScore cumulativeScore = cumulativeMBTIScoreRepository.findByChildProfileId(childProfileId)
                .orElseThrow(() -> new IllegalArgumentException("점수가 존재하지 않습니다."));

        cumulativeScore.resetScores();
    }

    @Transactional
    public CumulativeMBTIScore updateCumulativeMBTIScore(Long childProfileId, MBTIScore mbtiScore) {
        log.info("Update CumulativeMBTIScore ChildProfile ID: {}", childProfileId);
        CumulativeMBTIScore cumulativeScore = cumulativeMBTIScoreRepository.findByChildProfileId(childProfileId)
                .orElseThrow(() -> new IllegalArgumentException("점수가 존재하지 않습니다."));

        cumulativeScore.resetScores();
        return cumulativeScore.updateScores(mbtiScore);
    }

    @Transactional
    public void updateCumulativeMBTIScore(Long childProfileId, List<BookMBTI> bookMBTIS, double changeScore) {
        log.info("Update CumulativeMBTIScore ChildProfile ID: {}", childProfileId);
        CumulativeMBTIScore cumulativeScore = cumulativeMBTIScoreRepository.findByChildProfileId(childProfileId)
                .orElseThrow(() -> new IllegalArgumentException("점수가 존재하지 않습니다."));

        for (BookMBTI bookMBTI : bookMBTIS) {
            MBTI mbti = bookMBTI.getMbti();
            cumulativeScore.updateScores(mbti, changeScore);
        }
    }

    @Transactional
    public void updateGenreAndTopicScores(ChildProfile childProfile, Book book, double changedScore) {
        Genre genre = book.getGenre();
        List<BookTopic> bookTopics = book.getBookTopics();

        GenreScore genreScore = genreScoreRepository.findByChildProfileAndGenre(childProfile.getId(), genre.getId())
                .orElseThrow(() -> new EntityNotFoundException(childProfile.getId()));
        genreScore.updateScore(changedScore);

        for (BookTopic bookTopic : bookTopics) {
            TopicScore topicScore = topicScoreRepository.findByChildProfileAndTopic(childProfile.getId(),
                            bookTopic.getTopic().getId())
                    .orElseThrow(() -> new EntityNotFoundException(childProfile.getId()));
            topicScore.updateScore(changedScore);
        }

        log.info("Update Genre and Topic scores - ChildProfile ID: {}", childProfile.getId());
    }

    @Transactional
    public void bulkUpdateScores(Long childProfileId, ScoreUpdateEventDto aggregatedEvent) {
        boolean active = TransactionSynchronizationManager.isActualTransactionActive();
        log.info("Before bulk update, transaction active: {}", active);

        aggregatedEvent.getGenreDeltas().forEach((genreId, delta) -> {
            log.info("Updating GenreScore for childProfileId: {}, genreId: {}, delta: {}", childProfileId, genreId, delta);
            try {
                int updatedRows = genreScoreRepository.bulkUpdateScore(childProfileId, genreId, delta);
                log.info("Updated {} rows for GenreScore (childProfileId: {}, genreId: {})", updatedRows, childProfileId, genreId);
            } catch (Exception e) {
                log.error("Error during bulkUpdateScore for childProfileId: {}, genreId: {}, delta: {}", childProfileId, genreId, delta, e);
                throw e;
            }
        });

        aggregatedEvent.getTopicDeltas().forEach((topicId, delta) -> {
            log.info("Updating TopicScore for childProfileId: {}, topicId: {}, delta: {}", childProfileId, topicId, delta);
            try {
                int updatedRows = topicScoreRepository.bulkUpdateScore(childProfileId, topicId, delta);
                log.info("Updated {} rows for TopicScore (childProfileId: {}, topicId: {})", updatedRows, childProfileId, topicId);
            } catch (Exception e) {
                log.error("Error during bulkUpdateScore for TopicScore for childProfileId: {}, topicId: {}, delta: {}", childProfileId, topicId, delta, e);
                throw e;
            }
        });

        try {
            int updatedRows = cumulativeMBTIScoreRepository.bulkUpdateScore(childProfileId, aggregatedEvent.getCumulativeDelta());
            log.info("Updated {} rows for CumulativeMBTIScore (childProfileId: {})", updatedRows, childProfileId);
        } catch(Exception e) {
            log.error("Error during bulkUpdateScore for CumulativeMBTIScore for childProfileId: {}, delta: {}", childProfileId, aggregatedEvent.getCumulativeDelta(), e);
            throw e;
        }
    }
}
