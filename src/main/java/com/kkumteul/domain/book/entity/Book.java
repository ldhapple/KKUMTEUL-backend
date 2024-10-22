package com.kkumteul.domain.book.entity;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String publisher;
    private String price;
    private String page;
    private String age_group;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] bookImage;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<BookTopic> bookTopics = new ArrayList<>();


    @Builder
    public Book(String title, String author, String publisher, String price, String page, String summary, String age_group,
                byte[] bookImage, List<BookTopic> bookTopics) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.price = price;
        this.page = page;
        this.summary = summary;
        this.age_group = age_group;
        this.bookImage = bookImage;
        this.bookTopics = bookTopics;
    }
}
