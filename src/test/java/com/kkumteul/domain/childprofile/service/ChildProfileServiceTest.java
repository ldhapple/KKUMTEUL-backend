package com.kkumteul.domain.childprofile.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.given;

import com.kkumteul.domain.childprofile.dto.ChildProfileDto;
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
import com.kkumteul.domain.history.repository.ChildPersonalityHistoryRepository;
import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.entity.Topic;
import com.kkumteul.domain.personality.repository.GenreRepository;
import com.kkumteul.domain.personality.repository.TopicRepository;
import com.kkumteul.domain.user.entity.User;
import com.kkumteul.exception.ChildProfileNotFoundException;
import com.kkumteul.exception.RecommendationBookNotFoundException;
import java.util.Date;
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
class ChildProfileServiceTest {

    @Mock
    private ChildProfileRepository childProfileRepository;

    @Mock
    private CumulativeMBTIScoreRepository cumulativeMBTIScoreRepository;

    @Mock
    private GenreScoreRepository genreScoreRepository;

    @Mock
    private TopicScoreRepository topicScoreRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private ChildPersonalityHistoryRepository childPersonalityHistoryRepository;

    @InjectMocks
    private ChildProfileService childProfileService;

    @Test
    @DisplayName("유저 아이디의 자녀 프로필 리스트 조회 성공 테스트")
    void testGetChildProfiles() {

        Long userId = 1L;

        ChildProfile childProfile = ChildProfile.builder()
                .name("lee")
                .gender(Gender.FEMALE)
                .profileImage(new byte[]{})
                .build();

        List<ChildProfile> childProfiles = List.of(childProfile);

        given(childProfileRepository.findByUserId(userId)).willReturn(Optional.of(childProfiles));

        List<ChildProfileDto> results = childProfileService.getChildProfileList(userId);

        assertThat(results).isNotNull();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getChildName()).isEqualTo("lee");
    }

    @Test
    @DisplayName("조회된 자녀 프로필이 없을 때 예외 발생 테스트")
    void testGetChildProfilesNotFound() {
        Long userId = 1L;

        given(childProfileRepository.findByUserId(userId)).willReturn(Optional.empty());

        assertThrows(ChildProfileNotFoundException.class, () ->
                childProfileService.getChildProfileList(userId)
        );
    }

    @Test
    @DisplayName("자녀 프로필 검증 - 성공")
    void testValidateChildProfileSuccess() {
        Long validProfileId = 1L;

        given(childProfileRepository.findById(validProfileId)).willReturn(Optional.of(mock(ChildProfile.class)));

        assertDoesNotThrow(() -> childProfileService.validateChildProfile(validProfileId));
    }

    @Test
    @DisplayName("자녀 프로필 검증 - 실패")
    void testValidateChildProfileFailed() {
        Long invalidProfileId = 999L;

        given(childProfileRepository.findById(invalidProfileId)).willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> childProfileService.validateChildProfile(invalidProfileId));
    }

    @Test
    @DisplayName("자녀 프로필 생성 테스트")
    void testCreateChildProfileSuccess() {
        String name = "child";
        Gender gender = Gender.FEMALE;
        Date birthDate = new Date();
        byte[] profileImage = new byte[0];
        User user = mock(User.class);

        given(genreRepository.findAll()).willReturn(List.of(mock(Genre.class)));
        given(topicRepository.findAll()).willReturn(List.of(mock(Topic.class)));
        given(childProfileRepository.save(any(ChildProfile.class))).willReturn(ChildProfile.builder().name(name).build());

        ChildProfile createdProfile = childProfileService.createChildProfile(name, gender, birthDate, profileImage, user);

        assertThat(createdProfile).isNotNull();
        assertThat(createdProfile.getName()).isEqualTo(name);
        verify(childProfileRepository, times(1)).save(any(ChildProfile.class));
    }

    @Test
    @DisplayName("누적 MBTI 점수 초기화 테스트")
    void testResetCumulativeMBTIScore() {
        Long childProfileId = 1L;

        CumulativeMBTIScore cumulativeScore = mock(CumulativeMBTIScore.class);
        given(cumulativeMBTIScoreRepository.findByChildProfileId(childProfileId)).willReturn(Optional.of(cumulativeScore));

        childProfileService.resetCumulativeMBTIScore(childProfileId);

        verify(cumulativeScore, times(1)).resetScores();
    }

    @Test
    @DisplayName("누적 MBTI 점수 업데이트 테스트")
    void testUpdateCumulativeMBTIScore() {
        Long childProfileId = 1L;
        MBTIScore mbtiScore = mock(MBTIScore.class);

        CumulativeMBTIScore cumulativeScore = mock(CumulativeMBTIScore.class);
        given(cumulativeMBTIScoreRepository.findByChildProfileId(childProfileId)).willReturn(Optional.of(cumulativeScore));

        childProfileService.updateCumulativeMBTIScore(childProfileId, mbtiScore);

        verify(cumulativeScore, times(1)).updateScores(mbtiScore);
    }

    @Test
    @DisplayName("선호 장르 및 주제어 점수 초기화 테스트")
    void testResetFavoriteScores() {
        Long childProfileId = 1L;

        List<TopicScore> topicScores = List.of(mock(TopicScore.class));
        List<GenreScore> genreScores = List.of(mock(GenreScore.class));

        given(topicScoreRepository.findByChildProfileId(childProfileId)).willReturn(topicScores);
        given(genreScoreRepository.findByChildProfileId(childProfileId)).willReturn(genreScores);

        childProfileService.resetFavoriteScores(childProfileId);

        for (TopicScore topicScore : topicScores) {
            verify(topicScore, times(1)).resetScore();
        }
        for (GenreScore genreScore : genreScores) {
            verify(genreScore, times(1)).resetScore();
        }
    }
}