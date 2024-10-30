package com.kkumteul.domain.user.entity;

import jakarta.persistence.*;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.user.dto.UserUpdateRequestDto;
import jakarta.persistence.*;
import java.util.ArrayList;
import com.kkumteul.domain.event.entity.JoinEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import org.springframework.web.multipart.MultipartFile;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String nickName;

    @Column(nullable = true)
    private String phoneNumber;

    @Column(nullable = true)
    private LocalDate birthDate;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB", nullable = true)
    private byte[] profileImage;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = true)
    private Role role;

    @Setter
    private String refreshToken;

    @OneToMany(mappedBy = "user")
    List<JoinEvent> joinEventList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<ChildProfile> childProfileList = new ArrayList<>();

    @Builder
    public User(String username, String password, String nickName, String phoneNumber, LocalDate birthDate,
                byte[] profileImage, Role role, String refreshToken) {
        this.username = username;
        this.password = password;
        this.nickName = nickName;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.profileImage = profileImage;
        this.role = role;
        this.refreshToken = refreshToken;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

    public void update(UserUpdateRequestDto userUpdateRequestDto) {
        if (userUpdateRequestDto.getNickName() != null) {
            this.nickName = userUpdateRequestDto.getNickName();
        }
        if (userUpdateRequestDto.getPassword() != null) {
            this.password = userUpdateRequestDto.getPassword();
        }
        if (userUpdateRequestDto.getPhoneNumber() != null) {
            this.phoneNumber = userUpdateRequestDto.getPhoneNumber();
        }
    }

    // profileImage를 byte[]로 변환하여 저장하는 메소드
    public void updateProfileImage(byte[] multipartFile) {
        if(multipartFile != null) this.profileImage = multipartFile;

    }

}
