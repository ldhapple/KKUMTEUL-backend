package com.kkumteul.domain.survey.dto;

import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.mbti.entity.MBTIName;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MbtiDto {
    private String mbtiName;
    private String mbtiTitle;
    private String mbtiDescription;
    private byte[] mbtiImage;

    public static MbtiDto fromEntity(MBTI mbti) {
        return new MbtiDto(
                mbti.getMbti().name(),
                mbti.getTitle(),
                mbti.getDescription(),
                mbti.getMbtiImage()
        );
    }
}
