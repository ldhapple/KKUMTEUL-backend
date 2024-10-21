package com.kkumteul.domain.book.dto;

import com.kkumteul.domain.book.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class BookDto {
    private String title;
    private String author;
    private String publisher;
    private String price;
    private String page;
    private String ageGroup;
    private String summary;
    private byte[] bookImage;

    public static BookDto fromEntity(Book book) {
        return new BookDto(
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPrice(),
                book.getPage(),
                book.getAgeGroup(),
                book.getSummary(),
                book.getBookImage()
        );
    }
}
