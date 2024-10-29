package com.kkumteul.domain.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kkumteul.domain.book.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@ToString
public class RecommendBookDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long bookId;
    private String bookTitle;
    private byte[] bookImage;

    @JsonCreator
    public RecommendBookDto(
            @JsonProperty("bookId") Long bookId,
            @JsonProperty("bookTitle") String bookTitle,
            @JsonProperty("bookImage") byte[] bookImage) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookImage = bookImage;
    }

    public static RecommendBookDto fromEntity(Book book) {
        return new RecommendBookDto(
                book.getId(),
                book.getTitle(),
                book.getBookImage()
        );
    }
}
