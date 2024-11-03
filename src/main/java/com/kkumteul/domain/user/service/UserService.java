package com.kkumteul.domain.user.service;


import com.kkumteul.domain.user.dto.RegisterDto;
import com.kkumteul.domain.user.dto.UserResponseDto;
import com.kkumteul.domain.user.dto.UserUpdateRequestDto;
import com.kkumteul.domain.user.entity.Role;
import com.kkumteul.domain.user.entity.User;
import com.kkumteul.domain.user.repository.UserRepository;
import com.kkumteul.exception.UserNotFoundException;
import com.kkumteul.util.DateUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final DateUtil dateUtil;

    // 1. 유저 정보 조회
    public UserResponseDto getUser(Long userId) {
        log.info("user id: {}", userId);
        User user = userRepository.findByIdWithChildProfiles(userId)
                .orElseThrow(() -> new UserNotFoundException("user not found: " + userId));

        UserResponseDto userResponseDto = UserResponseDto.fromEntity(user);

        log.info("user information: {}", userResponseDto);
        return userResponseDto;
    }

    // 2. 유저 정보 수정
    public void updateUser(Long userId, UserUpdateRequestDto userUpdateRequestDto, MultipartFile profileImage)
            throws IOException {
        log.info("user: {}", userUpdateRequestDto);
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

    public void hasChildProfile(Long userId, Long childProfileId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("user not found: " + userId));
        boolean exists = user.getChildProfileList().stream()
                .anyMatch(childProfile -> Objects.equals(childProfile.getId(), childProfileId));

        if (!exists) {
            throw new IllegalArgumentException("잘못된 접근입니다. child profile Id:" + childProfileId);
        }
    }

    public boolean duplicateUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean duplicateNickname(String nickName) {
        return userRepository.existsByNickName(nickName);
    }

    public void register(RegisterDto registerDto) {
        String encodedPassword = bCryptPasswordEncoder.encode(registerDto.getPassword());

        User user = User.builder()
                .name(registerDto.getName())
                .username(registerDto.getUsername())
                .password(encodedPassword)
                .role(Role.ROLE_USER)
                .birthDate(dateUtil.parseBirthStringToDate(registerDto.getBirth()))
                .phoneNumber(registerDto.getPhoneNumber())
                .nickName(registerDto.getNickName())
                .build();

        userRepository.save(user);
    }
}

