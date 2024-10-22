package com.kkumteul.domain.recommendation.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookDataDto {
   private Long bookId;
   private String title;
   private String author;
   private List<TopicDto> topics;
   private GenreDto genreDto;
   private double score; // 필터링 점수

    @Builder
    public BookDataDto(Long bookId, String title, String author, GenreDto genreDto, List<TopicDto> topics) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genreDto = genreDto;
        this.topics = topics;
    }

    // 점수 누적
    public void addScore(double newScore) {
        this.score += newScore;
    }
}
