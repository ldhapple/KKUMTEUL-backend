package com.kkumteul.domain.childprofile.dto;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChildProfileDto {

    private Long profileId;
    private String childName;

    public static ChildProfileDto fromEntity(ChildProfile childProfile) {
        return new ChildProfileDto(
                childProfile.getId(),
                childProfile.getName()
        );
    }
}
