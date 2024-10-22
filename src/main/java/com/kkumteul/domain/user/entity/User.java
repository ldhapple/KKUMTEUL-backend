package com.kkumteul.domain.user.entity;

import com.kkumteul.domain.event.entity.JoinEvent;
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
    List<JoinEvent> joinEventList = new ArrayList<>();

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
}
