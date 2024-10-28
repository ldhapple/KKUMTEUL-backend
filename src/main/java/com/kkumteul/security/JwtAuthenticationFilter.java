package com.kkumteul.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;


import java.io.IOException;
import java.security.Key;

@Component
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService) {
        super(authenticationManager);
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);
        Key accessKey = jwtTokenProvider.getAccessSigningKey();

        try {
            if (token != null && jwtTokenProvider.validateToken(token, accessKey)) {
                String userId = jwtTokenProvider.getUserIdFromToken(token, accessKey);
                var userDetails = userDetailsService.loadUserByUsername(userId);

                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    // 디버깅 로그 추가
                    System.out.println("Validating token: " + token);
                    System.out.println("Authenticated user: " + userDetails.getUsername() + ", Authorities: " + userDetails.getAuthorities());
                }
            }
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우 로그 추가 및 응답 설정
            System.out.println("Expired JWT token: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Access token expired. Please refresh your token.");
            return;
        } catch (JwtException | IllegalArgumentException e) {
            // 기타 JWT 예외 처리
            System.out.println("Invalid JWT token or no token provided");
        }

        filterChain.doFilter(request, response);
    }
}