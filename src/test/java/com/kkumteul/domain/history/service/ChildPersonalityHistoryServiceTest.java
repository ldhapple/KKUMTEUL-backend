package com.kkumteul.domain.history.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.history.entity.FavoriteGenre;
import com.kkumteul.domain.history.entity.FavoriteTopic;
import com.kkumteul.domain.history.entity.HistoryCreatedType;
import com.kkumteul.domain.history.entity.MBTIScore;
import com.kkumteul.domain.history.repository.ChildPersonalityHistoryRepository;
import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.entity.Topic;
import com.kkumteul.domain.personality.repository.GenreRepository;
import com.kkumteul.domain.personality.repository.TopicRepository;
import com.kkumteul.exception.ChildProfileNotFoundException;
import com.kkumteul.exception.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChildPersonalityHistoryServiceTest {

    @Mock
    private ChildPersonalityHistoryRepository historyRepository;

    @Mock
    private ChildProfileRepository childProfileRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private ChildPersonalityHistoryService historyService;

    @Test
    @DisplayName("진단 히스토리 삭제 테스트")
    void testDeleteDiagnosisHistorySuccess() {
        Long childProfileId = 1L;
        ChildPersonalityHistory history = mock(ChildPersonalityHistory.class);

        given(historyRepository.findHistoryByChildProfileIdAndHistoryCreatedType(childProfileId, HistoryCreatedType.DIAGNOSIS))
                .willReturn(Optional.of(history));

        historyService.deleteDiagnosisHistory(childProfileId);

        verify(historyRepository, times(1)).delete(history);
    }

    @Test
    @DisplayName("새로운 히스토리 생성 테스트")
    void testCreateHistorySuccess() {
        Long childProfileId = 1L;
        MBTIScore mbtiScore = mock(MBTIScore.class);
        ChildProfile childProfile = mock(ChildProfile.class);

        given(childProfileRepository.findById(childProfileId)).willReturn(Optional.of(childProfile));

        ChildPersonalityHistory history = historyService.createHistory(childProfileId, mbtiScore, HistoryCreatedType.DIAGNOSIS);

        assertThat(history).isNotNull();
        assertThat(history.getMbtiScore()).isEqualTo(mbtiScore);

        //Mock으로 생성된 childProfile이 addHistory()를 1번 호출했는지 검증한다.
        verify(childProfile, times(1)).addHistory(history);
    }

    @Test
    @DisplayName("최신 히스토리 조회 테스트")
    void testGetLatestHistorySuccess() {
        Long childProfileId = 1L;
        ChildPersonalityHistory history = mock(ChildPersonalityHistory.class);

        given(historyRepository.findTopByChildProfileIdOrderByCreatedAtDesc(childProfileId)).willReturn(history);

        ChildPersonalityHistory result = historyService.getLatestHistory(childProfileId);

        assertThat(result).isEqualTo(history);
    }

    @Test
    @DisplayName("선호 장르 추가 테스트")
    void testAddFavoriteGenreSuccess() {
        Long historyId = 1L;
        Long genreId = 2L;

        ChildPersonalityHistory history = mock(ChildPersonalityHistory.class);
        Genre genre = mock(Genre.class);

        given(historyRepository.findById(historyId)).willReturn(Optional.of(history));
        given(genreRepository.findById(genreId)).willReturn(Optional.of(genre));

        historyService.addFavoriteGenre(historyId, genreId);

        verify(history, times(1)).addFavoriteGenre(any(FavoriteGenre.class));
    }

    @Test
    @DisplayName("선호 토픽 추가 테스트")
    void testAddFavoriteTopicSuccess() {
        Long historyId = 1L;
        Long topicId = 2L;

        ChildPersonalityHistory history = mock(ChildPersonalityHistory.class);
        Topic topic = mock(Topic.class);

        given(historyRepository.findById(historyId)).willReturn(Optional.of(history));
        given(topicRepository.findById(topicId)).willReturn(Optional.of(topic));

        historyService.addFavoriteTopic(historyId, topicId);

        verify(history, times(1)).addFavoriteTopic(any(FavoriteTopic.class));
    }

    @Test
    @DisplayName("존재하지 않는 프로필로 이력 생성 시 예외 발생 테스트")
    void testCreateHistoryProfileNotFound() {
        Long childProfileId = 999L;
        MBTIScore mbtiScore = mock(MBTIScore.class);

        given(childProfileRepository.findById(childProfileId)).willReturn(Optional.empty());

        assertThrows(ChildProfileNotFoundException.class, () ->
                historyService.createHistory(childProfileId, mbtiScore, HistoryCreatedType.DIAGNOSIS)
        );
    }

    @Test
    @DisplayName("존재하지 않는 장르 추가 시 예외 발생 테스트")
    void testAddFavoriteGenre_GenreNotFound() {
        Long historyId = 1L;
        Long genreId = 999L;

        ChildPersonalityHistory history = mock(ChildPersonalityHistory.class);

        given(historyRepository.findById(historyId)).willReturn(Optional.of(history));
        given(genreRepository.findById(genreId)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                historyService.addFavoriteGenre(historyId, genreId)
        );
    }
}