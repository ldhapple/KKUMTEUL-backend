package com.kkumteul.domain.history.entity;

import com.kkumteul.domain.personality.entity.Genre;
import com.kkumteul.domain.personality.entity.Topic;
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
public class ChildPersonalityHistoryTopic {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id")
    private ChildPersonalityHistory history;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Builder
    public ChildPersonalityHistoryTopic(ChildPersonalityHistory history, Topic topic) {
        this.history = history;
        this.topic = topic;
    }
}
