package com.kkumteul.domain.history.dto;


import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.history.entity.HistoryCreatedType;
import com.kkumteul.domain.mbti.entity.MBTIName;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChildPersonalityHistoryDto {
    private Long historyId;
    private MBTIName mbti;
    private String mbtiTitle;
    private byte[] mbtiImage;
    private LocalDateTime createdAt;
    private HistoryCreatedType historyCreatedType;

    public static ChildPersonalityHistoryDto fromEntity(ChildPersonalityHistory history) {
        return new ChildPersonalityHistoryDto(
                history.getId(),
                history.getMbtiScore().getMbti().getMbti(),
                history.getMbtiScore().getMbti().getTitle(),
                history.getMbtiScore().getMbti().getMbtiImage(),
                history.getCreatedAt(),
                history.getHistoryCreatedType()
        );
    }


}
