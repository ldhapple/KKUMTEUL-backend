package com.kkumteul.domain.HuggingFace.dto;

import com.kkumteul.domain.book.entity.Book;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HuggingRequestDto {
    private Long bookId;
    private String title;
    private String summary;

    public HuggingRequestDto(String title, String summary, Long bookId) {
        this.bookId = bookId;
        this.title = title;
        this.summary = summary;
    }
}
