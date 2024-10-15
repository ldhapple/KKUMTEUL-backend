package com.kkumteul.domain.history.entity;


import com.kkumteul.domain.childprofile.entity.ChildProfile;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
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

    private LocalDateTime createdAt;
    private HistoryCreatedType historyCreatedType;

    @Builder
    public ChildPersonalityHistory(ChildProfile childProfile, LocalDateTime createdAt,
                                   HistoryCreatedType historyCreatedType) {
        this.childProfile = childProfile;
        this.createdAt = createdAt;
        this.historyCreatedType = historyCreatedType;
    }
}
