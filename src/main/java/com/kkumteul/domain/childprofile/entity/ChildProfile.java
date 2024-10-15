package com.kkumteul.domain.childprofile.entity;

import com.kkumteul.domain.mbti.entity.MBTIScore;
import com.kkumteul.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.util.Date;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChildProfile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Date birthDate;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] profileImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mbti_score_id")
    private MBTIScore mbtiScore;

    @Builder
    public ChildProfile(String name, Gender gender, Date birthDate, byte[] profileImage, User user,
                        MBTIScore mbtiScore) {
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.profileImage = profileImage;
        this.user = user;
        this.mbtiScore = mbtiScore;
    }
}
