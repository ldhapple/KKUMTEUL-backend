package com.kkumteul.domain.recommendation.entity;


import com.kkumteul.domain.book.entity.Book;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recommendation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isLike;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_profile_id")
    private ChildProfile childProfile;

    @Builder
    public Recommendation(boolean isLike, Book book, ChildProfile childProfile) {
        this.isLike = isLike;
        this.book = book;
        this.childProfile = childProfile;
    }
}
