package com.kkumteul.domain.book.dto;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.book.entity.BookTopic;
import com.kkumteul.domain.personality.entity.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class AdminGetBookDetailResponseDto {
    private Long id;
    private byte[] image;
    private String title;
    private String publisher;
    private String author;
    private String price;
    private String ageGroup;
    private String bookGenre;
    private List<String> bookTopicList;
    private String bookMBTI;
    private String summary;
    private String page;

    public static AdminGetBookDetailResponseDto fromEntity(final Book book) {
        return new AdminGetBookDetailResponseDto(
                book.getId(),
                book.getBookImage(),
                book.getTitle(),
                book.getPublisher(),
                book.getAuthor(),
                book.getPrice(),
                book.getAgeGroup(),
                book.getBookGenre().getName(),
                book.getBookTopics().stream()
                        .map(BookTopic::getTopic)
                        .map(Topic::getName)
                        .toList(),
                book.getBookMBTIs().get(0)
                        .getMbti()
                        .getMbti()
                        .name(),
                book.getSummary(),
                book.getPage()
        );
    }
}
