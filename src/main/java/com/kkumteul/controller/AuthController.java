package com.kkumteul.controller;

import com.kkumteul.dto.AuthenticationRequest;
import com.kkumteul.dto.AuthenticationResponse;
import com.kkumteul.domain.user.entity.User; // User 엔티티 import 추가
import com.kkumteul.security.JwtTokenProvider;
import com.kkumteul.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    // 회원가입 엔드포인트
    @PostMapping("/signup")
    public ResponseEntity<String> register(@RequestBody AuthenticationRequest request) {
        // UserService의 registerUser 메서드를 호출하고, 반환된 User 객체를 사용
        User user = userService.registerUser(request.getUsername(), request.getPassword(), request.getUsername(), request.getPhoneNumber());

        return ResponseEntity.ok("User registered successfully with username: " + user.getUsername());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        try {
            // 사용자 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            // JWT 토큰 발급
            String token = jwtTokenProvider.createToken(authentication.getName());

            // 응답 DTO로 감싸서 반환
            return ResponseEntity.ok(new AuthenticationResponse(token));
        } catch (AuthenticationException e) {
            // 로그인 실패 시 401 상태 코드와 에러 메시지 반환
            return ResponseEntity.status(401).body(new AuthenticationResponse("Invalid login credentials"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // 클라이언트 측에서 JWT 토큰을 제거하도록 안내
        return ResponseEntity.ok("Logout successful. Please remove your token on client side.");
    }
}