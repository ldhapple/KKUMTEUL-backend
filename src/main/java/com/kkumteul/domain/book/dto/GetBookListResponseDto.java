package com.kkumteul.domain.book.dto;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.personality.entity.Topic;
import jakarta.persistence.Tuple;
import java.util.Arrays;
import java.util.Collections;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.kkumteul.domain.book.entity.BookTopic;

import java.util.List;

@Getter
@NoArgsConstructor
public class GetBookListResponseDto {
    private Long bookId;
    private String bookTitle;
    private byte[] bookImage;
    private List<String> topicNames;

    @Builder
    public GetBookListResponseDto(Long bookId, String bookTitle, byte[] bookImage, List<String> topicNames) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookImage = bookImage;
        this.topicNames = topicNames;
    }

    public static GetBookListResponseDto from(final Book book) {
        final GetBookListResponseDto bookListResponseDto = new GetBookListResponseDto();

        bookListResponseDto.bookId = book.getId();
        bookListResponseDto.bookTitle = book.getTitle();
        bookListResponseDto.bookImage = book.getBookImage();
        bookListResponseDto.topicNames = book.getBookTopics().stream()
                .map(BookTopic::getTopic)
                .map(Topic::getName)
                .toList();

        return bookListResponseDto;
    }

    public static GetBookListResponseDto create(final Tuple tuple, List<String> topicNames) {
        return GetBookListResponseDto.builder()
                .bookId(tuple.get("bookId", Long.class))
                .bookTitle(tuple.get("bookTitle", String.class))
                .bookImage(tuple.get("bookImage", byte[].class))
                .topicNames(topicNames)
                .build();
    }

    public static GetBookListResponseDto create(final Tuple tuple) {
        return GetBookListResponseDto.builder()
                .bookId(tuple.get("bookId", Long.class))
                .bookTitle(tuple.get("bookTitle", String.class))
                .bookImage(tuple.get("bookImage", byte[].class))
                .topicNames(Arrays.asList(tuple.get("topicNames", String.class).split(", ")))
                .build();
    }
}

