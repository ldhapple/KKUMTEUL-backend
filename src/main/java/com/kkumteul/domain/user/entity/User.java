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
import org.springframework.web.multipart.MultipartFile;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
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
//        if (userUpdateRequestDto.getProfileImage() != null) {
//            this.profileImage = userUpdateRequestDto.getProfileImage();
//        }
        if (userUpdateRequestDto.getNickName().isEmpty()) {
            this.nickName = userUpdateRequestDto.getNickName();
        }
        if (userUpdateRequestDto.getPassword().isEmpty()) {
            this.password = userUpdateRequestDto.getPassword();
        }
        if (userUpdateRequestDto.getPhoneNumber().isEmpty()) {
            this.phoneNumber = userUpdateRequestDto.getPhoneNumber();
        }
    }

    // profileImage를 byte[]로 변환하여 저장하는 메소드
    public void updateProfileImage(byte[] multipartFile) {
        this.profileImage = multipartFile;

    }

}
