package com.kkumteul.domain.book.entity;

import com.kkumteul.domain.personality.entity.Genre;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

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

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] bookImage;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Genre genre;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    @BatchSize(size = 10)
    private Set<BookTopic> bookTopics = new HashSet<>();

    @Builder
    public Book(String title, String author, String publisher, String price, String page, String summary,
                byte[] bookImage) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.price = price;
        this.page = page;
        this.summary = summary;
        this.bookImage = bookImage;
    }
}
