package com.kkumteul.domain.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JoinEventRequestDto {
    private Long eventId;
    private String ticket; // 발급 받은 티켓 (ticket-1 형태)
    private String name; // 유저 이름
    private String phoneNumber;
}