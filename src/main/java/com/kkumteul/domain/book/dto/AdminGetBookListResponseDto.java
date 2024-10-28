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
public class AdminGetBookListResponseDto {
    private Long id;
    private byte[] image;
    private String title;
    private String author;
    private String publisher;
    private String bookGenre;
    private String ageGroup;
    private List<String> bookTopicList;
    private String bookMBTI;

    public static AdminGetBookListResponseDto fromEntity(final Book book) {
        return new AdminGetBookListResponseDto(
                book.getId(),
                book.getBookImage(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getBookGenre().getName(),
                book.getAgeGroup(),
                book.getBookTopics().stream()
                        .map(BookTopic::getTopic)
                        .map(Topic::getName)
                        .toList(),
                book.getBookMBTIs().get(0)
                        .getMbti()
                        .getMbti()
                        .name()
        );
    }
}
