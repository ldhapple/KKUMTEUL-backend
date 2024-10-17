package com.kkumteul.domain.book.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import java.util.Date;
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

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] bookImage;

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
