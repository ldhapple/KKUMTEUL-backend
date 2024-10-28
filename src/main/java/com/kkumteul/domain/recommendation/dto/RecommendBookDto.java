package com.kkumteul.domain.recommendation.dto;

import com.kkumteul.domain.book.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendBookDto {

    private Long bookId;
    private String bookTitle;
    private byte[] bookImage;

    public static RecommendBookDto fromEntity(Book book) {
        return new RecommendBookDto(
                book.getId(),
                book.getTitle(),
                book.getBookImage()
        );
    }
}
