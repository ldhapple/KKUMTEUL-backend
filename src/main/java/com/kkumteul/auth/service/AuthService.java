package com.kkumteul.auth.service;

import com.kkumteul.exception.TokenExpiredException;
import com.kkumteul.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;

    public Map<String, String> refreshAccessToken(String refreshToken) {
        if (jwtUtil.isExpired(refreshToken)) {
            throw new TokenExpiredException();
        }

        Long userId = jwtUtil.getUserId(refreshToken);
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createAccessToken(userId, username, role, 60 * 60 * 1000L);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("accessToken", newAccessToken);

        return responseBody;
    }
}
