package com.kkumteul.domain.childprofile.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.given;

import com.kkumteul.domain.childprofile.dto.ChildProfileDto;
import com.kkumteul.domain.childprofile.dto.ChildProfileInsertRequestDto;
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
import com.kkumteul.domain.user.repository.UserRepository;
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
    private GenreRepository genreRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private ChildPersonalityHistoryRepository childPersonalityHistoryRepository;

    @Mock
    private UserRepository userRepository;

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
    @DisplayName("자녀 등록 성공 테스트")
    void insertChildProfile_success() {
        //given
        ChildProfile childProfile = ChildProfile.builder()
                .name("child1")
                .profileImage("image".getBytes())
                .birthDate(new Date())
                .gender(Gender.FEMALE)
                .build();

        //stub
        given(childProfileRepository.save(childProfile)).willReturn(childProfile);

        //when
        ChildProfile savedChildProfile = childProfileRepository.save(childProfile);

        //then
        assertEquals("child1", savedChildProfile.getName());
    }

    @Test
    @DisplayName("자녀 등록 실패 테스트 - 이름 누락")
    void insertChildProfile_missingName_fail() {
        //given
        Long userId = 1L;
        User user = User.builder()
                .username("user1")
                .nickName("nickname1")
                .phoneNumber("01012345678")
                .build();

        ChildProfileInsertRequestDto dto = new ChildProfileInsertRequestDto(
                null, // 이름 누락
                "FEMALE",
                "220907"
        );

        //stub
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        //when & then
        assertThrows(IllegalArgumentException.class, () -> {
            childProfileService.insertChildProfile(userId, null,  dto);
        });
    }

    @Test
    @DisplayName("자녀 삭제 성공 테스트")
    void deleteChildProfile_success() {
        //given
        Long childProfileId = 1L;
        ChildProfile childProfile = ChildProfile.builder()
                .name("child1")
                .profileImage("image".getBytes())
                .birthDate(new Date())
                .gender(Gender.FEMALE)
                .build();
        childProfileRepository.save(childProfile);

        //stub
        given(childProfileRepository.findById(childProfileId)).willReturn(Optional.of(childProfile));

        //when
        childProfileService.deleteChildProfile(childProfileId);

        //then
        assertFalse(childProfileRepository.findById(childProfile.getId()).isPresent(),
                "자녀 프로필이 삭제되어야 합니다.");

    }

    @Test
    @DisplayName("자녀 삭제 실패 테스트")
    void deleteChildProfile_notFound_fail() {
        //given
        Long childProfileId = 1L;

        //stub
        given(childProfileRepository.findById(childProfileId)).willReturn(Optional.empty());

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            childProfileService.deleteChildProfile(childProfileId);
        });

        //then
        assertEquals("childProfile not found: " + childProfileId, exception.getMessage());
    }
}