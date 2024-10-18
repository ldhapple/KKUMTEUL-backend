package com.kkumteul.domain.mbti.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MBTI {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MBTIName mbti;

    private String title;
    private String description;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] mbtiImage;

    @Builder
    public MBTI(MBTIName mbti, String title, String description, byte[] mbtiImage) {
        this.mbti = mbti;
        this.title = title;
        this.description = description;
        this.mbtiImage = mbtiImage;
    }
}
