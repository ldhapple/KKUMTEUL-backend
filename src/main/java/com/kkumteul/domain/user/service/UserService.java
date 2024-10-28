package com.kkumteul.domain.user.service;


import com.kkumteul.domain.user.dto.UserResponseDto;
import com.kkumteul.domain.user.dto.UserUpdateRequestDto;
import com.kkumteul.domain.user.entity.User;
import com.kkumteul.domain.user.repository.UserRepository;
import com.kkumteul.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    // 1. 유저 정보 조회
    public UserResponseDto getUser(Long userId) {
        log.info("user id: {}", userId);
        User user = userRepository.findByIdWithChildProfiles(userId)
                .orElseThrow(() -> new UserNotFoundException("user not found: " + userId));

        UserResponseDto userResponseDto = UserResponseDto.fromEntity(user);
        userResponseDto.setProfileImageBase64(userResponseDto.getProfileImageBase64());

        log.info("user information: {}", userResponseDto);
        return userResponseDto;
    }

    // 2. 유저 정보 수정
    public void updateUser(Long userId, UserUpdateRequestDto userUpdateRequestDto, MultipartFile profileImage) throws IOException {
        log.info("user: {}" , userUpdateRequestDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("user not found: " + userId));

        // TODO: 비밀번호 암호화하는 로직 추가
        if (profileImage != null) {
            byte[] byteProfileImage = profileImage.getBytes();
            user.updateProfileImage(byteProfileImage);
        }
        user.update(userUpdateRequestDto);

    }

    // 3. 유저 탈퇴
    public void deleteUser(Long userId) {
        log.info("user id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("user not found: " + userId));

        log.info("user info: {}", user);
        userRepository.delete(user);
    }
}

