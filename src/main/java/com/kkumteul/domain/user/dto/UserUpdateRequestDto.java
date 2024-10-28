package com.kkumteul.domain.user.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto {
//    private MultipartFile profileImage;
    private String nickName;
    private String password;
    private String phoneNumber;
}