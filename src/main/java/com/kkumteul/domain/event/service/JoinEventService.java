package com.kkumteul.domain.event.service;

import com.kkumteul.domain.event.dto.EventResultResponseDto;
import com.kkumteul.domain.event.dto.JoinEventRequestDto;
import com.kkumteul.domain.event.entity.Event;
import com.kkumteul.domain.event.entity.JoinEvent;
import com.kkumteul.domain.event.repository.EventRepository;
import com.kkumteul.domain.event.repository.JoinEventRepository;
import com.kkumteul.domain.user.entity.User;
import com.kkumteul.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
    private final JoinEventRepository joinEventRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public String joinEvent(Long userId, JoinEventRequestDto joinEventRequestDto) {
        String winnersSetKey = "winners"; // 이름, 전화번호가 요청되었을때 저장할 set
        String activeTicketsKey = "active_tickets";
        String duplicatedKey = "duplicate";
        String ticketKey = joinEventRequestDto.getTicket();

        // 이름 전화번호 중복 체크 (1:jung:01000000000:1)
        String winnerEntry =
                userId + ":" + joinEventRequestDto.getName() + ":" + joinEventRequestDto.getPhoneNumber() + ":"
                        + joinEventRequestDto.getEventId();

        String duplicatedValue = joinEventRequestDto.getName() + ":" + joinEventRequestDto.getPhoneNumber() + ":"
                + joinEventRequestDto.getEventId();

        Boolean isDuplicate = template.opsForSet().isMember(duplicatedKey, duplicatedValue);

        if (Boolean.TRUE.equals(isDuplicate)) {
            return "이미 해당 이름과 전화번호로 이벤트에 참여함";
        }

        Boolean isTicketValid = template.opsForSet().isMember(activeTicketsKey, ticketKey);

        if (Boolean.FALSE.equals(isTicketValid)) {
            return "세션이 만료되었습니다. 다시 응모해주세요!";
        }

        // 이름과 전화번호가 입력되었을 경우 winners 에 저장
        template.opsForSet().add(winnersSetKey, winnerEntry);
        template.opsForSet().add(duplicatedKey, duplicatedValue);
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

    // 이벤트 당첨자 리스트 반환
    public List<EventResultResponseDto> getYesterdayEventResults() {
        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime startOfHour = today.plusHours(12);
        LocalDateTime endOfHour = today.plusHours(14); //2시

        List<JoinEvent> events = joinEventRepository.findEventsAroundOnePM(startOfHour, endOfHour);
        return events.stream()
                .map(EventResultResponseDto::fromEntity)
                .toList();

    }

    public void setEventWinners() {
        String winnersSetKey = "winners";
        Set<String> winners = template.opsForSet().members(winnersSetKey);

        if (winners == null || winners.isEmpty()) {
            log.info("당첨자 목록이 비어 있습니다.");
            return;
        }

        for (String winnerEntry : winners) {
            String[] parts = winnerEntry.split(":");
            if (parts.length != 4) {
                log.warn("잘못된 형식의 당첨자 데이터: {}", winnerEntry);
                continue;
            }

            Long userId = Long.valueOf(parts[0]);
            String username = parts[1];
            String phoneNumber = parts[2];
            Long eventId = Long.valueOf(parts[3]);

            User user = userRepository.findById(userId).orElse(null);
            Event event = eventRepository.findById(eventId).orElse(null);

            if (user == null || event == null) {
                log.warn("사용자 또는 이벤트 정보를 찾을 수 없습니다. userId: {}, eventId: {}", userId, eventId);
                continue;
            }

            JoinEvent joinEvent = JoinEvent.builder()
                    .user(user)
                    .event(event)
                    .name(username)
                    .phoneNumber(phoneNumber)
                    .createdAt(LocalDateTime.now())
                    .isWin(true)
                    .build();

            joinEventRepository.save(joinEvent);
            log.info("JoinEvent 저장 완료 - userId: {}, eventId: {}", userId, eventId);
        }

        template.delete(winnersSetKey);
    }
}