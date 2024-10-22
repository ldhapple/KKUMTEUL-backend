package com.kkumteul.domain.recommendation.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class TopicDto {
    private Long topicId;
    private String topicName;
}
