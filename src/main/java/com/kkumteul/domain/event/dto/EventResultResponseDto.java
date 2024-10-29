package com.kkumteul.domain.event.dto;

import com.kkumteul.domain.event.entity.JoinEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EventResultResponseDto {
    private Long eventId;
    private boolean isWin;
    private LocalDateTime createdAt;
    public static EventResultResponseDto fromEntity(JoinEvent joinEvent) {
        return new EventResultResponseDto(
                joinEvent.getEvent().getId(),
                joinEvent.isWin(),
                joinEvent.getCreatedAt()
        );
    }

}
