package com.kkumteul.domain.event.entity;


import com.kkumteul.domain.user.entity.User;
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
public class JoinEvent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    private String name;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private boolean isWin;

    @Builder
    public JoinEvent(User user, Event event, String name, String phoneNumber, LocalDateTime createdAt, boolean isWin) {
        this.user = user;
        this.event = event;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.isWin = isWin;
    }
}
