package com.kkumteul.auth.controller;

import com.kkumteul.auth.service.AuthService;
import com.kkumteul.dto.AuthenticationRequest;
import com.kkumteul.dto.AuthenticationResponse;
import com.kkumteul.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.ExpiredJwtException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> register(@RequestBody AuthenticationRequest request) {
        authService.registerUser(
                request.getUsername(), request.getPassword(), request.getUsername(), request.getPhoneNumber());
        return ResponseEntity.ok("User registered successfully with username: " + request.getUsername());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            String userId = request.getUsername(); // 로그인한 사용자의 ID를 직접 사용
            String accessToken = jwtTokenProvider.createAccessToken(userId, "ROLE_USER");
            String refreshToken = jwtTokenProvider.createRefreshToken(userId);

            authService.updateRefreshToken(userId, refreshToken);

            return ResponseEntity.ok(new AuthenticationResponse(accessToken, refreshToken));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthenticationResponse("Invalid login credentials"));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshAccessToken(@RequestBody Map<String, String> tokenRequest) {
        String refreshToken = tokenRequest.get("refreshToken");
        try {
            if (jwtTokenProvider.validateRefreshToken(refreshToken)) {
                String userId = jwtTokenProvider.getUserIdFromToken(refreshToken, jwtTokenProvider.getRefreshSigningKey());
                if (authService.isRefreshTokenValid(userId, refreshToken)) {
                    String newAccessToken = jwtTokenProvider.createAccessToken(userId, "ROLE_USER");
                    return ResponseEntity.ok(new AuthenticationResponse(newAccessToken, refreshToken));
                }
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthenticationResponse("Refresh token expired. Please log in again."));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthenticationResponse("Invalid refresh token"));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody Map<String, String> tokenRequest) {
        String username = tokenRequest.get("username");
        String key = "refreshToken:" + username;
        authService.updateRefreshToken(username, null); // Redis에서 refresh token 삭제
        return ResponseEntity.ok("Logout successful. Please remove your token on client side.");
    }
}
