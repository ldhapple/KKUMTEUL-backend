package com.kkumteul.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RegisterDto {

    private String username;
    private String password;
    private String name;
    private String nickName;
    private String phoneNumber;
    private String birth;
}
