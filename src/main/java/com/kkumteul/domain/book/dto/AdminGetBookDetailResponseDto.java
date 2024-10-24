package com.kkumteul.domain.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AdminGetBookListResponseDto {
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private String bookGenre;
    private String ageGroup;
    private List<String> bookTopicList;
    private String bookMBTI;
}
