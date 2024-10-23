package com.kkumteul.domain.personality.entity;

import com.kkumteul.domain.book.entity.BookGenre;
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
public class Genre {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] image;

    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookGenre> bookGenres = new ArrayList<>();

    @Builder
    public Genre(String name, byte[] image, List<BookGenre> bookGenres) {
        this.name = name;
        this.image = image;
        this.bookGenres = bookGenres;
    }
}
