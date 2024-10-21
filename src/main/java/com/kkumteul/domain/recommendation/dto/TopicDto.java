package com.kkumteul.domain.recommendation.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TopicDto {
    private Long topicId;
    private String topicName;
}
