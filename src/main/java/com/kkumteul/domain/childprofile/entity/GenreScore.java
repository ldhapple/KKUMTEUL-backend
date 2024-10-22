package com.kkumteul.domain.childprofile.entity;

import com.kkumteul.domain.history.entity.ChildPersonalityHistory;
import com.kkumteul.domain.personality.entity.Genre;
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
public class GenreScore {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_profile_id")
    private ChildProfile childProfile;

    private double score;

    @Builder
    public GenreScore(Genre genre, double score) {
        this.genre = genre;
        this.score = score;
    }

    public void setChildProfile(ChildProfile childProfile) {
        this.childProfile = childProfile;
    }

    public void resetScore() {
        this.score = 0;
    }

    public void updateScore(double score) {
        this.score += score;
    }
}
