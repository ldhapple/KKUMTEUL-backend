package com.kkumteul.domain.history.entity;


import com.kkumteul.domain.childprofile.entity.ChildProfile;
import com.kkumteul.domain.mbti.entity.MBTIScore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    // 자녀 선호 장르 히스토리 리스트
    @OneToMany(mappedBy = "history", cascade = CascadeType.ALL)
    List<ChildPersonalityHistoryGenre> childPersonalityHistoryGenreList = new ArrayList<>();

    // 자녀 선호 주제어 히스토리 리스트
    @OneToMany(mappedBy = "history", cascade = CascadeType.ALL)
    List<ChildPersonalityHistoryTopic> childPersonalityHistoryTopicList = new ArrayList<>();

    @Builder
    public ChildPersonalityHistory(ChildProfile childProfile, LocalDateTime createdAt, boolean isDeleted,
                                   HistoryCreatedType historyCreatedType) {
        this.childProfile = childProfile;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
        this.historyCreatedType = historyCreatedType;
    }
}
