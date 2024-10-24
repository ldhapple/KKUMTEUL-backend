package com.kkumteul.domain.book.dto;

import com.kkumteul.domain.book.entity.BookLike;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookLikeDto {
    private Long bookId;
    private String bookTitle;
    private byte[] bookImage;

    public static BookLikeDto fromEntity(BookLike bookLike) {
        return new BookLikeDto(
                bookLike.getBook().getId(),
                bookLike.getBook().getTitle(),
                bookLike.getBook().getBookImage()
        );
    }
}
