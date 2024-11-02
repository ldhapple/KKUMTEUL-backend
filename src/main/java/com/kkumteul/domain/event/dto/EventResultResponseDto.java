package com.kkumteul.domain.event.dto;

import com.kkumteul.domain.event.entity.JoinEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventResultResponseDto {
    private String name;
    private String phoneNumber;
    public static EventResultResponseDto fromEntity(JoinEvent joinEvent) {
        return new EventResultResponseDto(
                joinEvent.getName(),
                joinEvent.getPhoneNumber()
        );
    }

}
