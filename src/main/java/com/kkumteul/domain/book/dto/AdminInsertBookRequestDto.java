package com.kkumteul.domain.book.dto;

import com.kkumteul.domain.mbti.entity.MBTI;
import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.entity.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class AdminInsertBookRequestDto {
    // book
    private String title;
    private String author;
    private String publisher;
    private String price;
    private String page;
    private String ageGroup;
    private String summary;
    private byte[] bookImage;

    // bookMbti
    private MBTI bookMbti;

    // bookGenre
    private List<Genre> bookGenreList;

    // bookTopic
    private List<Topic> bookTopicList;
}
