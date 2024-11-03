package com.kkumteul.domain.childprofile.dto;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Base64;

@Getter
@AllArgsConstructor
public class ChildProfileDto {

    private Long profileId;
    private String childName;
    private byte[] childProfileImage;

    public static ChildProfileDto fromEntity(ChildProfile childProfile) {
        return  new ChildProfileDto(
                childProfile.getId(),
                childProfile.getName(),
                childProfile.getProfileImage()
        );
    }
}
