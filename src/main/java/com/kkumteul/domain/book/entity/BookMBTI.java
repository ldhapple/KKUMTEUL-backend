package com.kkumteul.domain.book.entity;


import com.kkumteul.domain.mbti.entity.MBTI;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bookMBTI", uniqueConstraints = { @UniqueConstraint(columnNames = {"book_id"}) })
public class BookMBTI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mbti_id")
    private MBTI mbti;

    @Builder
    public BookMBTI(Book book, MBTI mbti) {
        this.book = book;
        this.mbti = mbti;
    }
}
