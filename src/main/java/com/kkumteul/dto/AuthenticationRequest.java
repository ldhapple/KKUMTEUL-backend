package com.kkumteul.dto;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String username;
    private String password;
    private String phoneNumber;
}

// username과 password를 클라이언트로부터 받는 DTO