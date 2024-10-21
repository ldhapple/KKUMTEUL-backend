package com.kkumteul.domain.history.entity;


import com.kkumteul.domain.book.entity.BookTopic;
import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.mbti.entity.MBTIScore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
public class ChildPersonalityHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_profile_id")
    private ChildProfile childProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mbti_score_id")
    private MBTIScore mbtiScore;

    private LocalDateTime createdAt;
    private boolean isDeleted;
    private HistoryCreatedType historyCreatedType;

    @OneToMany(mappedBy = "history", fetch = FetchType.LAZY)
    @BatchSize(size = 10)
    private Set<ChildPersonalityHistoryGenre> historyGenres = new HashSet<>();

    @OneToMany(mappedBy = "history", fetch = FetchType.LAZY)
    @BatchSize(size = 10)
    private Set<ChildPersonalityHistoryTopic> historyTopics = new HashSet<>();

    @Builder
    public ChildPersonalityHistory(ChildProfile childProfile, LocalDateTime createdAt, boolean isDeleted,
                                   HistoryCreatedType historyCreatedType) {
        this.childProfile = childProfile;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
        this.historyCreatedType = historyCreatedType;
    }
}
