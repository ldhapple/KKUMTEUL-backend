package com.kkumteul.domain.user.dto;

import com.kkumteul.domain.user.entity.User;
import lombok.*;

import java.util.Date;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long userId;
    private String username;
    private byte[] profileImage;
    private String nickName;
    private String phoneNumber;
    private Date birthDate;

    public static UserDto fromEntity(User user) {
        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getProfileImage(),
            user.getNickName(),
            user.getPhoneNumber(),
            user.getBirthDate()
        );
    }
}
