package com.kkumteul.domain.event.service;

import com.kkumteul.domain.event.dto.EventRequestDto;
import com.kkumteul.domain.event.dto.EventResultResponseDto;
import com.kkumteul.domain.event.entity.Event;
import com.kkumteul.domain.event.entity.JoinEvent;
import com.kkumteul.domain.event.repository.EventRepository;
import com.kkumteul.domain.event.repository.JoinEventRepository;
import com.kkumteul.domain.user.entity.User;
import com.kkumteul.domain.user.respository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableCaching
public class EventService {

    private final JoinEventRepository joinEventRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public void insertJoinEvent(Long userId, EventRequestDto eventRequestDto) {
        log.info("user id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + userId));

        Event event = getEventById(eventRequestDto.getEventId());
//        Event event = eventRepository.findById(eventRequestDto.getEventId())
//                .orElseThrow(() -> new IllegalArgumentException("event not found: " + eventRequestDto.getEventId()));

        LocalDateTime now = LocalDateTime.now();
        isEventInProgress(event, now);

        boolean isValidUser = isValidUser(user, eventRequestDto);
        log.info("Is valid user: {}", isValidUser);
        if(!isValidUser) {
            throw new IllegalArgumentException("입력한 정보가 회원 정보와 일치한지 확인해주세요.");
        }

        boolean hasUserJoined = joinEventRepository.existsByUserAndEvent(user, event);
        log.info("Has user joined: {}", hasUserJoined);
        if(hasUserJoined) {
            throw new IllegalArgumentException("이미 참여한 이벤트입니다.");
        }

        JoinEvent joinEvent = JoinEvent.builder()
                .user(user)
                .name(eventRequestDto.getName())
                .phoneNumber(eventRequestDto.getPhoneNumber())
                .createdAt(now)
                .event(event)
                .isWin(false)
                .build();

        JoinEvent savedJoinEvent = joinEventRepository.save(joinEvent);
        log.info("Check saved join event user name: {}", savedJoinEvent.getUser().getUsername());
    }

    private boolean isValidUser(User user, EventRequestDto eventRequestDto) {
        return user.getUsername().equals(eventRequestDto.getName()) && user.getPhoneNumber().equals(eventRequestDto.getPhoneNumber());
    }

    private void isEventInProgress(Event event, LocalDateTime now) {
        if (event.getStartDate().isAfter(now)) {
            long minutesLeft = Duration.between(now, event.getStartDate()).toMinutes();
            throw new IllegalArgumentException("이벤트가 곧 시작됩니다. 조금만 기다려 주세요!");
        }

        if (event.getExpiredDate().isBefore(now)) {
            throw new IllegalArgumentException("종료된 이벤트입니다.");
        }

        log.info("Event in progress");
    }

    public List<EventResultResponseDto> getJoinEventResults(Long userId) {
        log.info("user id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + userId));

        List<JoinEvent> joinEventList = user.getJoinEventList();

        return joinEventList.stream().map(EventResultResponseDto::fromEntity).toList();
    }

    @Cacheable(value = "event", key = "#eventId")
    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("event not found: " + eventId));
    }
}
