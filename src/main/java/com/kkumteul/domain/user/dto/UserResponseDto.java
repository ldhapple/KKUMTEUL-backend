package com.kkumteul.domain.user.dto;

import com.kkumteul.domain.childprofile.dto.ChildProfileDetailDto;
import com.kkumteul.domain.user.entity.User;
import lombok.*;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String username;
    private byte[] profileImage;
    private String profileImageBase64;
    private String nickName;
    private String phoneNumber;
    private Date birthDate;
    private List<ChildProfileDetailDto> childProfileList;

    public static UserResponseDto fromEntity(User user) {
        List<ChildProfileDetailDto> childProfiles = user.getChildProfileList().stream()
                .map(ChildProfileDetailDto::fromEntity)
                .toList();

        return new UserResponseDto(
                user.getUsername(),
                user.getProfileImage(),
                null,
                user.getNickName(),
                user.getPhoneNumber(),
                user.getBirthDate(),
                childProfiles
        );
    }

    public void setProfileImageBase64(String base64) {
        this.profileImageBase64 = base64;
    }

    public String getProfileImageBase64() {
        return Base64.getEncoder().encodeToString(profileImage);
    }

}
