package com.kkumteul.dto;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreUpdateEventDto {

    private Long childProfileId;
    private Map<Long, Double> genreDeltas = new HashMap<>();
    private Map<Long, Double> topicDeltas = new HashMap<>();
    private double cumulativeDelta;
}
