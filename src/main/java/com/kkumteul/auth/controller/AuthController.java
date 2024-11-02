package com.kkumteul.auth.controller;

import com.kkumteul.auth.service.AuthService;
import com.kkumteul.util.ApiUtil;
import com.kkumteul.util.ApiUtil.ApiSuccess;
import com.kkumteul.util.CookieUtil;
import com.kkumteul.util.redis.RedisUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final CookieUtil cookieUtil;
    private final AuthService authService;
    private final RedisUtil redisUtil;

    @PostMapping("/refresh")
    public ApiSuccess<?> refreshAccessToken(HttpServletRequest request) {

        String refreshToken = cookieUtil.getCookieValue("refreshToken", request);

        Map<String, String> responseBody = authService.refreshAccessToken(refreshToken);

        return ApiUtil.success(responseBody);
    }

    @PostMapping("/logout")
    public ApiSuccess<?> logout(HttpServletRequest request, HttpServletResponse response) {
        //클라이언트 측 세션 스토리지에서 Access 토큰 삭제도 필요.
        String refreshToken = cookieUtil.getCookieValue("refreshToken", request);
        Long userId = authService.getUserIdFromToken(refreshToken);

        redisUtil.deleteRefreshToken(userId.toString());

        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);

        return ApiUtil.success("로그아웃이 완료되었습니다.");
    }
}
