package com.kkumteul.domain.user.entity;

import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.user.dto.UserUpdateRequestDto;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String nickName;
    private String phoneNumber;
    private Date birthDate;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] profileImage;

    @OneToMany(mappedBy = "user")
    List<ChildProfile> childProfileList = new ArrayList<>();

    @Builder
    public User(String username, String password, String nickName, String phoneNumber, Date birthDate,
                byte[] profileImage) {
        this.username = username;
        this.password = password;
        this.nickName = nickName;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.profileImage = profileImage;
    }

    public void update(UserUpdateRequestDto userUpdateRequestDto) {
        this.profileImage = userUpdateRequestDto.getProfileImage();
        this.nickName = userUpdateRequestDto.getNickName();
        this.password = userUpdateRequestDto.getPassword();
        this.phoneNumber = userUpdateRequestDto.getPhoneNumber();
    }
}
