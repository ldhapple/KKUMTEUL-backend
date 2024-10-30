package com.kkumteul.service;

import com.kkumteul.domain.user.entity.User;
import com.kkumteul.domain.user.entity.Role;
import com.kkumteul.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

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
        String key = "refreshToken:" + username;
        System.out.println("Saving Refresh Token for user: " + key + ", token: " + refreshToken);
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofDays(7));
    }

    public boolean isRefreshTokenValid(String username, String refreshToken) {
        String key = "refreshToken:" + username;
        String storedToken = (String) redisTemplate.opsForValue().get(key);
        return refreshToken.equals(storedToken);
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

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}
