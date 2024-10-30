package com.kkumteul.controller;

import com.kkumteul.domain.user.entity.User;
import com.kkumteul.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUserProfile() {
        // SecurityContextHolder를 통해 인증 정보를 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(403).build(); // 인증되지 않았을 때 403 Forbidden 반환
        }
    }
}