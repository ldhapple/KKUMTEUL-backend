package com.kkumteul.domain.book.entity;

import com.kkumteul.domain.personality.entity.Genre;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
    private String ageGroup;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] bookImage;

    @ManyToOne(fetch = FetchType.LAZY)
    private Genre genre;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @BatchSize(size = 10)
    private List<BookTopic> bookTopics = new ArrayList<>();

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 10)
    private List<BookMBTI> bookMBTIS = new ArrayList<>();

    @Builder
    public Book(String title, String author, String publisher, String price, String page, String ageGroup,
                String summary, byte[] bookImage, Genre genre, List<BookTopic> bookTopics, List<BookMBTI> bookMBTIS) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.price = price;
        this.page = page;
        this.ageGroup = ageGroup;
        this.summary = summary;
        this.bookImage = bookImage;
        this.genre = genre;
        this.bookTopics = bookTopics;
        this.bookMBTIS = bookMBTIS;
    }

    public void update(byte[] bookImage, String title, String author, String publisher, String price, String page, String ageGroup,
                       String summary, Genre genre){
        if (bookImage != null) {
            this.bookImage = bookImage;
        }
        if (title != null) {
            this.title = title;
        }
        if (author != null) {
            this.author = author;
        }
        if (publisher != null) {
            this.publisher = publisher;
        }
        if (price != null) {
            this.price = price;
        }
        if (page != null) {
            this.page = page;
        }
        if (ageGroup != null) {
            this.ageGroup = ageGroup;
        }
        if (summary != null) {
            this.summary = summary;
        }
        if (genre != null) {
            this.genre = genre;
        }
    }
}
