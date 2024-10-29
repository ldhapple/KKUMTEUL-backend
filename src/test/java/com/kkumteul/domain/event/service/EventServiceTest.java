//package com.kkumteul.domain.event.service;
//
//import com.kkumteul.domain.event.dto.EventRequestDto;
//import com.kkumteul.domain.event.entity.Event;
//import com.kkumteul.domain.event.entity.JoinEvent;
//import com.kkumteul.domain.event.repository.JoinEventRepository;
//import com.kkumteul.domain.event.repository.EventRepository;
//import com.kkumteul.domain.user.entity.User;
//import com.kkumteul.domain.user.repository.UserRepository;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.dao.DataIntegrityViolationException;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.lenient;
//
//@ExtendWith(MockitoExtension.class)
//class EventServiceTest {
//
//    @InjectMocks
//    private EventService eventService;
//
//    @Mock
//    private JoinEventRepository joinEventRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private EventRepository eventRepository;
//
//    private User user;
//    private Event event;
//
//    @BeforeEach
//    public void setup() {
//        user = User.builder()
//                .username("name")
//                .phoneNumber("01000000000")
//                .password("password")
//                .build();
//
//        event = Event.builder()
//                .name("eventName")
//                .expiredDate(LocalDateTime.now().plusDays(1))
//                .startDate(LocalDateTime.now().minusMinutes(1))
//                .build();
//    }
//
//    @Test
//    @DisplayName("이벤트 참여 성공 테스트")
//    public void insertJoinEvent_success() {
//        //given
//        Long userId = 1L;
//        User user = User.builder()
//                .username("name")
//                .phoneNumber("01000000000")
//                .password("password")
//                .build();
//
//        Event event = Event.builder()
//                .name("eventName")
//                .expiredDate(LocalDateTime.now().plusDays(1))
//                .startDate(LocalDateTime.now().minusMinutes(1))
//                .build();
//        userRepository.save(user);
//
//        //when
//        EventRequestDto eventRequestDto = new EventRequestDto(event.getId(), "01000000000", "name");
//
//        given(userRepository.findById(userId)).willReturn(Optional.of(user));
//        given(eventRepository.findById(eventRequestDto.getEventId())).willReturn(Optional.of(event));
////        given(joinEventRepository.existsByUserAndEvent(user, event)).willReturn(false);
//
//        JoinEvent joinEventToSave = JoinEvent.builder()
//                .user(user)
//                .name(eventRequestDto.getUsername())
//                .phoneNumber(eventRequestDto.getPhoneNumber())
//                .createdAt(LocalDateTime.now())
//                .event(event)
//                .isWin(false)
//                .build();
//        given(joinEventRepository.save(any(JoinEvent.class))).willReturn(joinEventToSave);
//
//        eventService.insertJoinEvent(userId, eventRequestDto);
//        given(joinEventRepository.count()).willReturn(1L);
//
//        //then
//        Assertions.assertEquals(1, joinEventRepository.count());
//    }
//
//    @Test
//    @DisplayName("이벤트 참여 실패 테스트 - 중복 참여")
//    public void insertJoinEvent_duplicated_fail() {
//        //given
//        User user = User.builder()
//                .username("name")
//                .phoneNumber("01000000000")
//                .password("password")
//                .build();
//
//        Event event = Event.builder()
//                .name("eventName")
//                .expiredDate(LocalDateTime.now().plusDays(1))
//                .startDate(LocalDateTime.now().minusMinutes(1))
//                .build();
//
//        //when
//        EventRequestDto eventRequestDto = new EventRequestDto(event.getId(), "01000000000", "name");
//
//        //stub
//        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
//        given(eventRepository.findById(eventRequestDto.getEventId())).willReturn(Optional.of(event));
////        given(joinEventRepository.existsByUserAndEvent(user, event)).willReturn(true);
//
//        //then
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            eventService.insertJoinEvent(user.getId(), eventRequestDto);
//        });
//    }
//
//    @Test
//    @DisplayName("이벤트 참여 실패 테스트 - 이벤트 종료")
//    public void insertJoinEvent_eventExpired_fail() {
//        //given
//        User user = User.builder()
//                .username("name")
//                .phoneNumber("01000000000")
//                .password("password")
//                .build();
//
//        Event event = Event.builder()
//                .name("eventName")
//                .expiredDate(LocalDateTime.now().minusMinutes(1))
//                .startDate(LocalDateTime.now().minusDays(1))
//                .build();
//
//        //when
//        EventRequestDto eventRequestDto = new EventRequestDto(event.getId(), "01000000000", "name");
//
//        //stub
//        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
//        given(eventRepository.findById(eventRequestDto.getEventId())).willReturn(Optional.of(event));
//
//        //then
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            eventService.insertJoinEvent(user.getId(), eventRequestDto);
//        });
//
//    }
//
//    @Test
//    @DisplayName("이벤트 참여 실패 테스트 - 이벤트 진행 전")
//    public void insertJoinEvent_eventStarted_fail() {
//        //given
//        User user = User.builder()
//                .username("name")
//                .phoneNumber("01000000000")
//                .password("password")
//                .build();
//
//        Event event = Event.builder()
//                .name("eventName")
//                .expiredDate(LocalDateTime.now())
//                .startDate(LocalDateTime.now().plusMinutes(2))
//                .build();
//
//        //when
//        EventRequestDto eventRequestDto = new EventRequestDto(event.getId(), "01000000000", "name");
//
//        //stub
//        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
//        given(eventRepository.findById(eventRequestDto.getEventId())).willReturn(Optional.of(event));
//
//        //then
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            eventService.insertJoinEvent(user.getId(), eventRequestDto);
//        });
//
//    }
//
//    @Test
//    @DisplayName("동시성 문제 테스트 - 중복 이벤트 참여")
//    void concurrentJoinEvent() throws InterruptedException {
//        // given
//        Long userId = 1L;
//        User user = User.builder()
//                .username("name")
//                .phoneNumber("01000000000")
//                .password("password")
//                .build();
//
//        Long eventId = 1L;
//        Event event = Event.builder()
//                .startDate(LocalDateTime.now().minusHours(1))
//                .expiredDate(LocalDateTime.now().plusHours(1))
//                .build();
//
//        EventRequestDto eventRequestDto = new EventRequestDto(eventId, "01000000000", "name");
//
//        given(userRepository.findById(userId)).willReturn(Optional.of(user));
//        given(eventRepository.findById(eventRequestDto.getEventId())).willReturn(Optional.of(event));
//
//        AtomicBoolean hasJoined = new AtomicBoolean(false);
////        given(joinEventRepository.existsByUserAndEvent(user, event)).willAnswer(invocation -> hasJoined.get());
//        given(joinEventRepository.save(any(JoinEvent.class))).willAnswer(invocation -> {
//            JoinEvent joinEventToSave = (JoinEvent) invocation.getArgument(0);
//
//            // 첫 번째 저장 후 hasJoined를 true로 설정
//            if (!hasJoined.get()) {
//                hasJoined.set(true); // 참여한 것으로 표시
//                return JoinEvent.builder()
//                        .user(joinEventToSave.getUser())
//                        .name(joinEventToSave.getName())
//                        .phoneNumber(joinEventToSave.getPhoneNumber())
//                        .createdAt(LocalDateTime.now())
//                        .event(joinEventToSave.getEvent())
//                        .isWin(false)
//                        .build();
//            } else {
//                throw new IllegalArgumentException("이미 참여한 이벤트입니다."); // 중복 참여
//            }
//        });
//
//        int threads = 200;
//        CountDownLatch doneSignal = new CountDownLatch(threads);
//        ExecutorService executorService = Executors.newFixedThreadPool(threads);
//        AtomicInteger successCount = new AtomicInteger();
//        AtomicInteger failCount = new AtomicInteger();
//
//        // when: 동시 실행을 위한 스레드 생성
//        for (int i = 0; i < threads; i++) {
//            executorService.execute(() -> {
//                try {
//                    eventService.insertJoinEvent(userId, eventRequestDto);
//                    successCount.incrementAndGet(); // 성공적으로 참여한 경우
//                } catch (IllegalArgumentException e) {
//                    // 중복 참여로 인한 예외 발생
//                    if (e.getMessage().contains("이미 참여한 이벤트입니다.")) {
//                        failCount.incrementAndGet();
//                    } else {
//                        throw e; // 다른 예외는 다시 던짐
//                    }
//                } finally {
//                    doneSignal.countDown(); // 스레드가 작업을 마쳤음을 알림
//                }
//            });
//        }
//
//        doneSignal.await(); // 모든 스레드가 종료될 때까지 대기
//        executorService.shutdown();
//
//        // then: 성공 횟수와 실패 횟수를 검증
//        assertThat(successCount.get()).isEqualTo(1); // 중복 참여로 1명만 성공
//        assertThat(failCount.get()).isEqualTo(199); // 나머지 9명은 실패해야 함
//    }
//
//
//}
