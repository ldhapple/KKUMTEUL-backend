package com.kkumteul.domain.childprofile.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.given;

import com.kkumteul.domain.childprofile.dto.ChildProfileDto;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.Gender;
import com.kkumteul.domain.childprofile.repository.ChildProfileRepository;
import com.kkumteul.exception.ChildProfileNotFoundException;
import com.kkumteul.exception.RecommendationBookNotFoundException;
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

    @InjectMocks
    private ChildProfileService childProfileService;

    @Test
    @DisplayName("유저 아이디의 자녀 프로필 조회 성공 테스트")
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
}