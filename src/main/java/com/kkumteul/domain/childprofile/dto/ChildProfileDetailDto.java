package com.kkumteul.domain.childprofile.dto;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.Gender;
import lombok.*;

import java.util.Base64;
import java.util.Date;

@Getter
@AllArgsConstructor
public class ChildProfileDetailDto {
    private Long childProfileId;
    private String childName;
    private Gender childGender;
    private Date childBirthDate;
    private byte[] childProfileImage;
    private String childProfileImageBase64;

    public static ChildProfileDetailDto fromEntity(ChildProfile childProfile) {
        ChildProfileDetailDto childProfileDetailDto = new ChildProfileDetailDto(
                childProfile.getId(),
                childProfile.getName(),
                childProfile.getGender(),
                childProfile.getBirthDate(),
                childProfile.getProfileImage(),
                null
        );

        if (childProfileDetailDto.getChildProfileImage() != null) {
            childProfileDetailDto.setChildProfileImageBase64(Base64.getEncoder().encodeToString(childProfileDetailDto.getChildProfileImage()));
        }

        return childProfileDetailDto;
    }

    public void setChildProfileImageBase64(String base64) {
        this.childProfileImageBase64 = base64;
    }

    public String getChildProfileImageBase64() {
        return childProfileImage != null ? Base64.getEncoder().encodeToString(childProfileImage) : null;

    }
}
