package com.kkumteul.domain.event.dto;

import com.kkumteul.domain.event.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EventDto {
    private Long eventId;
    private String eventName;
    private String eventDescription;
    private LocalDateTime startDate;
    private LocalDateTime expiredDate;

    public static EventDto fromEntity(Event event) {
        return new EventDto(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getStartDate(),
                event.getExpiredDate()
        );

    }
}
