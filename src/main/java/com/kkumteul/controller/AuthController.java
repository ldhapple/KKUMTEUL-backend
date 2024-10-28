package com.kkumteul.controller;

import com.kkumteul.dto.AuthenticationRequest;
import com.kkumteul.dto.AuthenticationResponse;
import com.kkumteul.security.JwtTokenProvider;
import com.kkumteul.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import java.util.Map;

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

    @PostMapping("/signup")
    public ResponseEntity<String> register(@RequestBody AuthenticationRequest request) {
        userService.registerUser(
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

            userService.updateRefreshToken(userId, refreshToken);

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
                String newAccessToken = jwtTokenProvider.createAccessToken(userId, "ROLE_USER");
                return ResponseEntity.ok(new AuthenticationResponse(newAccessToken, refreshToken));
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthenticationResponse("Refresh token expired. Please log in again."));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthenticationResponse("Invalid refresh token"));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout successful. Please remove your token on client side.");
    }
}