package com.kkumteul.domain.childprofile.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.CumulativeMBTIScore;
import com.kkumteul.domain.childprofile.entity.GenreScore;
import com.kkumteul.domain.childprofile.entity.TopicScore;
import com.kkumteul.domain.childprofile.repository.CumulativeMBTIScoreRepository;
import com.kkumteul.domain.childprofile.repository.GenreScoreRepository;
import com.kkumteul.domain.childprofile.repository.TopicScoreRepository;
import com.kkumteul.domain.history.entity.MBTIScore;
import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.entity.Topic;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonalityScoreServiceTest {

    @Mock
    private GenreScoreRepository genreScoreRepository;

    @Mock
    private TopicScoreRepository topicScoreRepository;

    @Mock
    private CumulativeMBTIScoreRepository cumulativeMBTIScoreRepository;

    @InjectMocks
    private PersonalityScoreService personalityScoreService;

    @Test
    @DisplayName("누적 점수 리셋 테스트")
    void testResetFavoriteScores() {
        Long childProfileId = 1L;
        List<TopicScore> topicScores = List.of(mock(TopicScore.class), mock(TopicScore.class));
        List<GenreScore> genreScores = List.of(mock(GenreScore.class), mock(GenreScore.class));

        when(topicScoreRepository.findByChildProfileId(childProfileId)).thenReturn(topicScores);
        when(genreScoreRepository.findByChildProfileId(childProfileId)).thenReturn(genreScores);

        personalityScoreService.resetFavoriteScores(childProfileId);

        topicScores.forEach(topicScore -> verify(topicScore, times(1)).resetScore());
        genreScores.forEach(genreScore -> verify(genreScore, times(1)).resetScore());
    }

    @Test
    @DisplayName("누적 선호 주제어 점수 변경 테스트")
    void testUpdateFavoriteGenresScore() {
        ChildProfile childProfile = mock(ChildProfile.class);
        List<Long> favoriteGenreIds = List.of(1L, 2L);
        GenreScore genreScore1 = mock(GenreScore.class);
        GenreScore genreScore2 = mock(GenreScore.class);
        Genre genre1 = mock(Genre.class);
        Genre genre2 = mock(Genre.class);

        when(genre1.getId()).thenReturn(1L);
        when(genre2.getId()).thenReturn(2L);
        when(genreScore1.getGenre()).thenReturn(genre1);
        when(genreScore2.getGenre()).thenReturn(genre2);
        when(childProfile.getGenreScores()).thenReturn(List.of(genreScore1, genreScore2));

        personalityScoreService.updateFavoriteGenresScore(childProfile, favoriteGenreIds);

        verify(genreScore1).updateScore(5.0);
        verify(genreScore2).updateScore(5.0);
    }

    @Test
    @DisplayName("선호 주제어 점수 변경 테스트")
    void testUpdateFavoriteTopicsScore() {
        ChildProfile childProfile = mock(ChildProfile.class);
        List<Long> favoriteTopicIds = List.of(1L, 2L);
        TopicScore topicScore1 = mock(TopicScore.class);
        TopicScore topicScore2 = mock(TopicScore.class);
        Topic topic1 = mock(Topic.class);
        Topic topic2 = mock(Topic.class);

        when(topic1.getId()).thenReturn(1L);
        when(topic2.getId()).thenReturn(2L);
        when(topicScore1.getTopic()).thenReturn(topic1);
        when(topicScore2.getTopic()).thenReturn(topic2);
        when(childProfile.getTopicScores()).thenReturn(List.of(topicScore1, topicScore2));

        personalityScoreService.updateFavoriteTopicsScore(childProfile, favoriteTopicIds);

        verify(topicScore1).updateScore(5.0);
        verify(topicScore2).updateScore(5.0);
    }

    @Test
    @DisplayName("누적 MBTI 점수 리셋 테스트")
    void testResetCumulativeMBTIScore() {
        Long childProfileId = 1L;
        CumulativeMBTIScore cumulativeScore = mock(CumulativeMBTIScore.class);

        when(cumulativeMBTIScoreRepository.findByChildProfileId(childProfileId)).thenReturn(Optional.of(cumulativeScore));

        personalityScoreService.resetCumulativeMBTIScore(childProfileId);

        verify(cumulativeScore).resetScores();
    }

    @Test
    @DisplayName("누적 MBTI 점수 존재하지 않을 경우 테스트")
    void testResetCumulativeMBTIScoreNotFound() {
        Long childProfileId = 999L;

        when(cumulativeMBTIScoreRepository.findByChildProfileId(childProfileId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> personalityScoreService.resetCumulativeMBTIScore(childProfileId));
    }

    @Test
    @DisplayName("누적 MBTI 점수 변경 테스트")
    void testUpdateCumulativeMBTIScore() {
        Long childProfileId = 1L;
        MBTIScore mbtiScore = mock(MBTIScore.class);
        CumulativeMBTIScore cumulativeScore = mock(CumulativeMBTIScore.class);

        when(cumulativeMBTIScoreRepository.findByChildProfileId(childProfileId)).thenReturn(Optional.of(cumulativeScore));
        when(cumulativeScore.updateScores(mbtiScore)).thenReturn(cumulativeScore);

        CumulativeMBTIScore result = personalityScoreService.updateCumulativeMBTIScore(childProfileId, mbtiScore);

        assertThat(result).isEqualTo(cumulativeScore);
        verify(cumulativeScore).updateScores(mbtiScore);
    }
}