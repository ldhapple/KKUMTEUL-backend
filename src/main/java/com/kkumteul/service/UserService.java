package com.kkumteul.service;

import com.kkumteul.domain.user.entity.User;
import com.kkumteul.domain.user.entity.Role;
import com.kkumteul.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String username, String password, String nickName, String phoneNumber) {
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickName(nickName)
                .phoneNumber(phoneNumber)
                .role(Role.ROLE_USER) // 기본 역할을 ROLE_USER로 설정
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public void updateRefreshToken(String username, String refreshToken) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public boolean isRefreshTokenValid(String username, String refreshToken) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return refreshToken.equals(user.getRefreshToken());
    }

    @Transactional
    public User createAdminUser(String username, String password, String nickName, String phoneNumber) {
        User admin = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickName(nickName)
                .phoneNumber(phoneNumber)
                .role(Role.ROLE_ADMIN) // 관리자 역할 설정
                .build();
        return userRepository.save(admin);
    }
}