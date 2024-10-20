package com.kkumteul.domain.user.service;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.Gender;
import com.kkumteul.domain.user.dto.UserResponseDto;
import com.kkumteul.domain.user.entity.User;
import com.kkumteul.domain.user.repository.UserRepository;
import com.kkumteul.exception.UserNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("유저 정보 조회 성공 테스트 - 자녀 존재")
    void getUser_isChildProfile_success() {
        //given
        Long userId = 1L;
        List<ChildProfile> childProfiles = new ArrayList<>();
        ChildProfile childProfile = ChildProfile.builder()
                .name("child1")
                .gender(Gender.FEMALE)
                .birthDate(new Date())
                .profileImage("image".getBytes())
                .user(null)
                .build();
        childProfiles.add(childProfile);

        User user = User.builder()
                .username("user1")
                .profileImage("image".getBytes())
                .nickName("nickname1")
                .phoneNumber("01012345678")
                .birthDate(new Date())
                .build();

        user.getChildProfileList().addAll(childProfiles);

        //stub
        given(userRepository.findByIdWithChildProfiles(userId)).willReturn(Optional.of(user));

        //when
        UserResponseDto userResponseDto = userService.getUser(userId);

        //then
        Assertions.assertEquals("user1", userResponseDto.getUsername());
        Assertions.assertEquals(1, userResponseDto.getChildProfileList().size());

    }

    @Test
    @DisplayName("유저 정보 조회 성공 테스트 - 자녀 없음")
    void getUser_isNotChildProfile_success() {
        //given
        Long userId = 1L;

        User user = User.builder()
                .username("user1")
                .profileImage("image".getBytes())
                .nickName("nickname")
                .phoneNumber("01012345678")
                .birthDate(new Date())
                .build();

        //stub
        given(userRepository.findByIdWithChildProfiles(userId)).willReturn(Optional.of(user));

        //when
        UserResponseDto userResponseDto = userService.getUser(userId);

        //then
        Assertions.assertEquals("user1", userResponseDto.getUsername());
        Assertions.assertTrue(userResponseDto.getChildProfileList().isEmpty(), "자녀 프로필 리스트가 비어 있어야 함");

    }

    @Test
    @DisplayName("유저 정보 조회 실패 테스트 - UserNotFound 예외 발생")
    void getUser_not_found_exception() {
        //given
        Long userId = 1L;

        //stub
        given(userRepository.findByIdWithChildProfiles(userId)).willReturn(Optional.empty());

        //when
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUser(userId);
        });

        //then
        Assertions.assertEquals("user not found: " + userId, exception.getMessage());

    }


}