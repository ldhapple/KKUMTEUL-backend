package com.kkumteul.domain.childprofile.dto;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.Gender;
import lombok.*;

import java.util.Date;

@Getter
@ToString
@AllArgsConstructor
public class ChildProfileDto {
    private Long childProfileId;
    private String childName;
    private Gender childGender;
    private Date childBirthDate;
    private byte[] childProfileImage;

    public static ChildProfileDto fromEntity(ChildProfile childProfile) {
        return new ChildProfileDto(
                childProfile.getId(),
                childProfile.getName(),
                childProfile.getGender(),
                childProfile.getBirthDate(),
                childProfile.getProfileImage()
        );
    }
}
