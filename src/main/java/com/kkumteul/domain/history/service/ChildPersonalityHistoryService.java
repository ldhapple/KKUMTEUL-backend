package com.kkumteul.domain.history.service;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.history.entity.FavoriteGenre;
import com.kkumteul.domain.history.entity.FavoriteTopic;
import com.kkumteul.domain.history.entity.HistoryCreatedType;
import com.kkumteul.domain.history.entity.MBTIScore;
import com.kkumteul.domain.history.repository.ChildPersonalityHistoryRepository;
import com.kkumteul.domain.childprofile.entity.CumulativeMBTIScore;
import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.entity.Topic;
import com.kkumteul.domain.personality.repository.GenreRepository;
import com.kkumteul.domain.personality.repository.TopicRepository;
import com.kkumteul.exception.ChildProfileNotFoundException;
import com.kkumteul.exception.EntityNotFoundException;
import com.kkumteul.exception.HistoryNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChildPersonalityHistoryService {

    private final ChildPersonalityHistoryRepository historyRepository;
    private final ChildProfileRepository childProfileRepository;
    private final GenreRepository genreRepository;
    private final TopicRepository topicRepository;


    public void deleteDiagnosisHistory(Long childProfileId) {
        Optional<ChildPersonalityHistory> diagnosisHistory = historyRepository.findHistoryByChildProfileIdAndHistoryCreatedType(
                childProfileId, HistoryCreatedType.DIAGNOSIS);

        diagnosisHistory.ifPresent(historyRepository::delete);
    }

    public ChildPersonalityHistory createHistory(Long childProfileId, MBTIScore mbtiScore, HistoryCreatedType type) {
        log.info("Create history ChildProfile ID: {}", childProfileId);
        ChildProfile childProfile = childProfileRepository.findById(childProfileId)
                .orElseThrow(() -> new ChildProfileNotFoundException(childProfileId));

        ChildPersonalityHistory history = ChildPersonalityHistory.builder()
                .mbtiScore(mbtiScore)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .historyCreatedType(type)
                .build();

        childProfile.addHistory(history);

        return history;
    }

    @Transactional(readOnly = true)
    public ChildPersonalityHistory getLatestHistory(Long childProfileId) {
        return historyRepository.findTopByChildProfileIdOrderByCreatedAtDesc(
                childProfileId);
    }

    public void addFavoriteGenre(Long historyId, Long genreId) {
        log.info("Add FavoriteGenre ID: {}, History ID: {}", genreId, historyId);
        ChildPersonalityHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new HistoryNotFoundException(historyId));

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException(genreId));

        FavoriteGenre favoriteGenre = FavoriteGenre.builder()
                .genre(genre)
                .build();

        history.addFavoriteGenre(favoriteGenre);
    }

    public void addFavoriteTopic(Long historyId, Long topicId) {
        log.info("Add FavoriteTopic ID: {}, History ID: {}", topicId, historyId);
        ChildPersonalityHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new HistoryNotFoundException(historyId));

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException(topicId));

        FavoriteTopic favoriteTopic = FavoriteTopic.builder()
                .topic(topic)
                .build();

        history.addFavoriteTopic(favoriteTopic);
    }

    @Transactional
    public void deleteHistory(Long historyId) {
        log.info("delete History Id: {}", historyId);

        ChildPersonalityHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new HistoryNotFoundException(historyId));

        history.delete();
    }
}
