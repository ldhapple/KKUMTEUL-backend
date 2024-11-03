package com.kkumteul.domain.book.dto;

import com.kkumteul.domain.mbti.entity.MBTIName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class AdminBookFilterResponseDto {
    private Long id;
    private byte[] image;
    private String title;
    private String author;
    private String publisher;
    private String bookGenre;
    private String ageGroup;
    private String bookTopicList;
    private MBTIName bookMBTI;
}
