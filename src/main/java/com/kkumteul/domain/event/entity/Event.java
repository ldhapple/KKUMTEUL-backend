package com.kkumteul.domain.event.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    private LocalDateTime startDate;
    private LocalDateTime expiredDate;

    @Builder
    public Event(String name, String description, LocalDateTime startDate, LocalDateTime expiredDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.expiredDate = expiredDate;
    }
}
