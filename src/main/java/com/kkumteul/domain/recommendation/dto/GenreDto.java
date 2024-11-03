package com.kkumteul.domain.recommendation.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@Builder
@ToString
public class GenreDto {
    private Long genreId;
    private String genreName;
}
