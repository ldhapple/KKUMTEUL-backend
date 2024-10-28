package com.kkumteul.domain.book.dto;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookTopic;
import com.kkumteul.domain.personality.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private String price;
    private String page;
    private String ageGroup;
    private String summary;
    private byte[] bookImage;
    private Genre genre;
    private List<BookTopic> bookTopics;

    public static BookDto fromEntity(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPrice(),
                book.getPage(),
                book.getAgeGroup(),
                book.getSummary(),
                book.getBookImage(),
                book.getGenre(),
                book.getBookTopics()
        );
    }
}
