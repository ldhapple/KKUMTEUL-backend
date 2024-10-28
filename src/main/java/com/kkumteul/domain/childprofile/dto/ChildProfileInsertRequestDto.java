package com.kkumteul.domain.childprofile.dto;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.childprofile.entity.Gender;
import com.kkumteul.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@AllArgsConstructor
public class ChildProfileInsertRequestDto {
    private String childName;
    private String childGender;
    private String childBirthDate;

    public static ChildProfile toEntity(ChildProfileInsertRequestDto childProfileInsertRequestDto, User user) throws ParseException {
        Gender gender = Gender.valueOf(childProfileInsertRequestDto.getChildGender().toUpperCase());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date parsedDate = formatter.parse(childProfileInsertRequestDto.getChildBirthDate());
        return ChildProfile.builder()
                .name(childProfileInsertRequestDto.getChildName())
                .gender(gender)
                .birthDate(parsedDate)
                .user(user)
                .build();
    }
}
