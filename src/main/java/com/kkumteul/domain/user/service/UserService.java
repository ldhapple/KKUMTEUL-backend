package com.kkumteul.domain.user.service;


import com.kkumteul.domain.user.dto.UserDto;
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
    public UserDto getUser(Long userId) {
        log.info("user id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("user not found: " + userId));

        UserDto userResponseDto = UserDto.fromEntity(user);
        log.info("user information: {}", userResponseDto);
        return userResponseDto;
    }
}

