package com.kkumteul.domain.personality.entity;

import com.kkumteul.domain.book.entity.BookTopic;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] image;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookTopic> bookTopics = new ArrayList<>();

    @Builder
    public Topic(String name, byte[] image, List<BookTopic> bookTopics) {
        this.name = name;
        this.image = image;
        this.bookTopics = bookTopics;
    }
}