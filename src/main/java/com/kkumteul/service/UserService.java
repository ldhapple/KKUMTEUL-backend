package com.kkumteul.service;

import com.kkumteul.domain.user.entity.Role;
import com.kkumteul.domain.user.entity.User;
import com.kkumteul.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@RequiredArgsConstructor  // Lombok이 자동으로 생성자 생성
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(String username, String rawPassword, String nickname, String phoneNumber) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 새 유저 생성 및 저장
        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .nickName(nickname)
                .phoneNumber(phoneNumber)
                .role(Role.ROLE_USER)  // 기본 역할을 USER로 설정
                .build();

        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    // 관리자 계정 생성 메서드
    public User createAdminUser(String username, String rawPassword, String nickname, String phoneNumber) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 관리자 유저 생성 및 저장
        User adminUser = User.builder()
                .username(username)
                .password(encodedPassword)
                .nickName(nickname)
                .phoneNumber(phoneNumber)
                .role(Role.ROLE_ADMIN)  // 역할을 ADMIN으로 설정
                .build();

        return userRepository.save(adminUser);
    }
}