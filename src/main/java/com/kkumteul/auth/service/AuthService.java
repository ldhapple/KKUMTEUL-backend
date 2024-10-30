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

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createAccessToken(username, role, 60 * 60 * 10L);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("accessToken", newAccessToken);

        return responseBody;
    }
}
