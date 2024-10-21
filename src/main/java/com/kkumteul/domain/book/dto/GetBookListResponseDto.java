package com.kkumteul.domain.book.dto;

import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.personality.entity.Topic;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.kkumteul.domain.book.entity.BookTopic;

import java.util.List;

@Getter
@NoArgsConstructor
public class GetBookListResponseDto {
    private Long bookId;
    private String bookTitle;
    private byte[] bookImage; // 또는 String으로 바꿔서 이미지 URL을 저장할 수도 있습니다.
    private List<String> topicNames; // Topic 이름 리스트

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
}

