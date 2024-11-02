package com.kkumteul.domain.childprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChildProfileInsertRequestDto {
    private String childName;
    private String childGender;
    private String childBirthDate;
}
