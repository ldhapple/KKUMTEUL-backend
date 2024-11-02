package com.kkumteul.domain.event.service;

import com.kkumteul.domain.event.dto.JoinEventRequestDto;
import jakarta.transaction.Transactional;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.kkumteul.domain.event.dto.JoinEventRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JoinEventService {
    private final RedisTemplate<String, String> template;

    public String joinEvent(Long userId, JoinEventRequestDto joinEventRequestDto) {
        String winnersSetKey = "winners"; // 이름, 전화번호가 요청되었을때 저장할 set
        String activeTicketsKey = "active_tickets";
        String ticketKey = joinEventRequestDto.getTicket();

        // 이름 전화번호 중복 체크 (1:jung:01000000000:1)
        String winnerEntry = userId + ":" + joinEventRequestDto.getName() + ":" + joinEventRequestDto.getPhoneNumber() + ":" + joinEventRequestDto.getEventId();

        Boolean isDuplicate = template.opsForSet().isMember(winnersSetKey, winnerEntry);

        if (Boolean.TRUE.equals(isDuplicate)) {
            return "이미 해당 이름과 전화번호로 이벤트에 참여함";
        }

        Boolean isTicketValid = template.opsForSet().isMember(activeTicketsKey, ticketKey);

        if (Boolean.FALSE.equals(isTicketValid)) {
            return "세션이 만료되었습니다. 다시 응모해주세요!";
        }

        // 이름과 전화번호가 입력되었을 경우 winners 에 저장
        template.opsForSet().add(winnersSetKey, winnerEntry);
        template.opsForSet().remove(activeTicketsKey, ticketKey);
        log.info("이벤트 참여 완료: " + winnerEntry);

        return "이름, 전화번호까지 입력해서 이벤트 참여 완료!!";
    }

    // 티켓 반환
    @Scheduled(fixedDelay = 600000) // 10분마다 실행 예시
    public void returnExpiredTickets() {
        Set<String> activeTickets = template.opsForSet().members("active_tickets");

        for (String ticket : activeTickets) {
            if (Boolean.FALSE.equals(template.hasKey(ticket))) {
                template.opsForList().rightPush("tickets", ticket);

                template.opsForSet().remove("active_tickets", ticket);

                log.info("만료 티켓이 반환되었습니다. ticket: {}", ticket);
            }
        }
    }
}