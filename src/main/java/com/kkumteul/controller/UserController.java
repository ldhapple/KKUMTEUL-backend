package com.kkumteul.controller;

import com.kkumteul.domain.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/protected-resource")
    public ResponseEntity<String> getProtectedResource(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok("Hello, " + username + "! This is a protected resource.");
    }

    @GetMapping("/dashboard")
    public ResponseEntity<String> getAdminDashboard(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok("Hello Admin, " + username + "! This is the admin dashboard.");
    }

    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userRepository.existsByUsername(username);
        return ResponseEntity.ok(exists); // 존재하면 true, 존재하지 않으면 false 반환
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickName) {
        boolean exists = userRepository.existsByNickName(nickName);
        return ResponseEntity.ok(exists); // 존재하면 true, 존재하지 않으면 false 반환
    }
}