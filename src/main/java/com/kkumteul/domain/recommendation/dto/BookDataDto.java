package com.kkumteul.domain.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookDataDto {
   private Long bookId;
   private String title;
   private String author;
   private List<TopicDto> topics;
   private GenreDto genreDto;

    @Builder
    public BookDataDto(Long bookId, String title, String author, GenreDto genreDto, List<TopicDto> topics) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genreDto = genreDto;
        this.topics = topics;
    }

}
