package com.kkumteul.domain.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class AdminCreateBookRequestDto {
    private String title;
    private String author;
    private String publisher;
    private String price;
    private String page;
    private String ageGroup;
    private String summary;
    private byte[] bookImage;

    private String bookMbti;
    private List<String> bookGenreList;
    private List<String> bookTopicList;
}
