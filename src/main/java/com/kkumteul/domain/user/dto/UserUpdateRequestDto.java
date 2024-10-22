package com.kkumteul.domain.user.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto {
    private byte[] profileImage;
    private String nickName;
    private String password;
    private String phoneNumber;
}