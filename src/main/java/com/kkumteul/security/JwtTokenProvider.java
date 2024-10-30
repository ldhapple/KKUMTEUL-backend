package com.kkumteul.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key accessSecretKey;
    private final Key refreshSecretKey;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public JwtTokenProvider(
            @Value("${jwt.access.secret}") String accessSecret,
            @Value("${jwt.refresh.secret}") String refreshSecret,
            @Value("${jwt.access.validity}") long accessTokenValidity,
            @Value("${jwt.refresh.validity}") long refreshTokenValidity) {
        this.accessSecretKey = new SecretKeySpec(accessSecret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
        this.refreshSecretKey = new SecretKeySpec(refreshSecret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String createAccessToken(String userId, String role) {
        return createToken(userId, role, accessSecretKey, accessTokenValidity);
    }

    public String createRefreshToken(String userId) {
        return createToken(userId, null, refreshSecretKey, refreshTokenValidity);
    }

    private String createToken(String userId, String role, Key secretKey, long validity) {
        Claims claims = Jwts.claims().setSubject(userId);
        if (role != null) {
            claims.put("role", role);
        }

        Date now = new Date();
        Date validityDate = new Date(now.getTime() + validity);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validityDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, Key secretKey) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: " + e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshSecretKey);
    }

    public String getUserIdFromToken(String token, Key secretKey) {
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: " + e.getMessage());
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token");
        }
    }

    public Key getAccessSigningKey() {
        return accessSecretKey;
    }

    public Key getRefreshSigningKey() {
        return refreshSecretKey;
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}