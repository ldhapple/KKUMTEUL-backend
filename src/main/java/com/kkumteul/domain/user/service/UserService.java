package com.kkumteul.domain.user.service;


import com.kkumteul.domain.user.dto.UserResponseDto;
import com.kkumteul.domain.user.dto.UserUpdateRequestDto;
import com.kkumteul.domain.user.entity.User;
import com.kkumteul.domain.user.repository.UserRepository;
import com.kkumteul.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 1. 유저 정보 조회
    public UserResponseDto getUser(Long userId) {
        log.info("user id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("user not found: " + userId));

        UserResponseDto userResponseDto = UserResponseDto.fromEntity(user);
        log.info("user information: {}", userResponseDto);
        return userResponseDto;
    }

    // 2. 유저 정보 수정
    public String updateUser(Long userId, UserUpdateRequestDto userUpdateRequestDto) {
        log.info("user: {}" , userUpdateRequestDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("user not found: " + userId));

        // TODO: 비밀번호 암호화하는 로직 추가

        user.update(userUpdateRequestDto);
        userRepository.save(user);

        return "user updated successfully";
    }

}

