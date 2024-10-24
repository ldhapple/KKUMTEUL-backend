package com.kkumteul.domain.recommendation.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookDataDto that = (BookDataDto) o;
        return Objects.equals(bookId, that.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId);
    }

}
