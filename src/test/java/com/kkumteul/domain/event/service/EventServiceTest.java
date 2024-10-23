package com.kkumteul.domain.event.service;

import com.kkumteul.domain.event.dto.EventRequestDto;
import com.kkumteul.domain.event.entity.Event;
import com.kkumteul.domain.event.entity.JoinEvent;
import com.kkumteul.domain.event.repository.JoinEventRepository;
import com.kkumteul.domain.event.repository.EventRepository;
import com.kkumteul.domain.user.entity.User;
import com.kkumteul.domain.user.respository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private JoinEventRepository joinEventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Test
    @DisplayName("이벤트 참여 성공 테스트")
    public void insertJoinEvent_success() {
        //given
        User user = User.builder()
                .username("name")
                .phoneNumber("01000000000")
                .password("password")
                .build();

        Event event = Event.builder()
                .name("eventName")
                .expiredDate(LocalDateTime.now().plusDays(1))
                .startDate(LocalDateTime.now().minusMinutes(1))
                .build();

        //when
        EventRequestDto eventRequestDto = new EventRequestDto(event.getId(), "01000000000", "name");

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(eventRepository.findById(eventRequestDto.getEventId())).willReturn(Optional.of(event));
        given(joinEventRepository.existsByUserAndEvent(user, event)).willReturn(false);

        JoinEvent joinEventToSave = JoinEvent.builder()
                .user(user)
                .name(eventRequestDto.getName())
                .phoneNumber(eventRequestDto.getPhoneNumber())
                .createdAt(LocalDateTime.now())
                .event(event)
                .isWin(false)
                .build();
        given(joinEventRepository.save(any(JoinEvent.class))).willReturn(joinEventToSave);

        eventService.insertJoinEvent(user.getId(), eventRequestDto);
        given(joinEventRepository.count()).willReturn(1L);

        //then
        Assertions.assertEquals(1, joinEventRepository.count());
    }

    @Test
    @DisplayName("이벤트 참여 실패 테스트 - 중복 참여")
    public void insertJoinEvent_duplicated_fail() {
        //given
        User user = User.builder()
                .username("name")
                .phoneNumber("01000000000")
                .password("password")
                .build();

        Event event = Event.builder()
                .name("eventName")
                .expiredDate(LocalDateTime.now().plusDays(1))
                .startDate(LocalDateTime.now().minusMinutes(1))
                .build();

        //when
        EventRequestDto eventRequestDto = new EventRequestDto(event.getId(), "01000000000", "name");

        //stub
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(eventRepository.findById(eventRequestDto.getEventId())).willReturn(Optional.of(event));
        given(joinEventRepository.existsByUserAndEvent(user, event)).willReturn(true);

        //then
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            eventService.insertJoinEvent(user.getId(), eventRequestDto);
        });
    }

    @Test
    @DisplayName("이벤트 참여 실패 테스트 - 이벤트 종료")
    public void insertJoinEvent_eventExpired_fail() {
        //given
        User user = User.builder()
                .username("name")
                .phoneNumber("01000000000")
                .password("password")
                .build();

        Event event = Event.builder()
                .name("eventName")
                .expiredDate(LocalDateTime.now().minusMinutes(1))
                .startDate(LocalDateTime.now().minusDays(1))
                .build();

        //when
        EventRequestDto eventRequestDto = new EventRequestDto(event.getId(), "01000000000", "name");

        //stub
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(eventRepository.findById(eventRequestDto.getEventId())).willReturn(Optional.of(event));

        //then
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            eventService.insertJoinEvent(user.getId(), eventRequestDto);
        });

    }

    @Test
    @DisplayName("이벤트 참여 실패 테스트 - 이벤트 진행 전")
    public void insertJoinEvent_eventStarted_fail() {
        //given
        User user = User.builder()
                .username("name")
                .phoneNumber("01000000000")
                .password("password")
                .build();

        Event event = Event.builder()
                .name("eventName")
                .expiredDate(LocalDateTime.now())
                .startDate(LocalDateTime.now().plusMinutes(2))
                .build();

        //when
        EventRequestDto eventRequestDto = new EventRequestDto(event.getId(), "01000000000", "name");

        //stub
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(eventRepository.findById(eventRequestDto.getEventId())).willReturn(Optional.of(event));

        //then
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            eventService.insertJoinEvent(user.getId(), eventRequestDto);
        });

    }


}
