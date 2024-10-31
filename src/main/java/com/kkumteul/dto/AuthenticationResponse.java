package com.kkumteul.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;

    public AuthenticationResponse(String message) {
        this.accessToken = message;
        this.refreshToken = null;
    }
}