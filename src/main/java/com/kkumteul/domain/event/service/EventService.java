package com.kkumteul.domain.event.service;

import com.kkumteul.domain.event.dto.EventDto;
import com.kkumteul.domain.event.dto.EventRequestDto;
import com.kkumteul.domain.event.entity.Event;
import com.kkumteul.domain.event.entity.JoinEvent;
import com.kkumteul.domain.event.repository.EventRepository;
import com.kkumteul.domain.event.repository.JoinEventRepository;
import com.kkumteul.domain.user.entity.User;
import com.kkumteul.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableCaching
public class EventService {

    private final JoinEventRepository joinEventRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RedisTemplate<String, String> template;

    private static final int QUEUE_LIMIT = 100; // 대기열 크기
    private static final int ISSUE_BATCH_SIZE = 10; // 한 번에 발급할 쿠폰 수

    @Transactional
    public void insertJoinEvent(Long userId, EventRequestDto eventRequestDto) {
        // 1. phoneNumber + username 조합으로 redis 에서 중복 확인을 한다.
        // 1-1 고유키 생성
        Long eventId = eventRequestDto.getEventId();
        String eventKey = "event" + eventId; // 이벤트 참여 요청 key
        String uniqueKey = eventRequestDto.getPhoneNumber() + eventRequestDto.getUsername(); // 전화번호 + 이름 key

        // 1-2 대기열 크기 확인 (불필요한 로직 개선)
        Long issuedJoinEventCount = template.opsForValue().increment("issuedJoinEvent", 1);

        if(issuedJoinEventCount == null || issuedJoinEventCount > QUEUE_LIMIT) {
            log.info("이벤트 참여 대기열이 가득 찼습니다.");
            template.opsForValue().decrement("issuedJoinEvent");
            return;
        }

        // 1-3 중복 확인 (phoneNumber + username)
        if (Boolean.TRUE.equals(template.opsForSet().isMember(eventKey + ":duplicates", uniqueKey))) {
//            log.info("이미 발급 요청이 있습니다 (phoneNumber & username): {}", uniqueKey);
            template.opsForValue().decrement("issuedJoinEvent");
            return;
        }

        // 1-4 중복 확인 userId
        if (template.opsForZSet().score(eventKey, String.valueOf(userId)) != null){
//            log.info("이미 발급 요청이 있습니다 (userId): {}", userId);
            template.opsForValue().decrement("issuedJoinEvent");
            return;
        }

        // 2. 중복 확인 후 대기열에 등록한다.
        if (issuedJoinEventCount <= QUEUE_LIMIT) {
            long score = System.currentTimeMillis();
            template.opsForZSet().add(eventKey, String.valueOf(userId), score);
            template.opsForSet().add(eventKey + ":duplicates", uniqueKey);
            log.info("요청이 접수되었습니다. userId: {}", userId);
        }

        // 3. 대기열이 가득 차면 처리 (100명)
        if (issuedJoinEventCount == QUEUE_LIMIT) {
            processQueue(eventId);
        }
    }

    public void processQueue(Long eventId) {
        String eventKey = "event"+eventId;
        Set<String> usersId = template.opsForZSet().range(eventKey, 0, QUEUE_LIMIT - 1);

        if (usersId != null && !usersId.isEmpty()) {
            for (String userIdStr : usersId) {
                Long userId = Long.valueOf(userIdStr);

                template.opsForSet().add("winners", String.valueOf(userId)); // Set 으로 해도됨
                log.info("쿠폰이 성공적으로 발급되었습니다. userId: {}", userId);

            }
            // 발급된 쿠폰을 Redis 에서 제거
            template.opsForZSet().remove(eventKey, usersId.toArray());
        }

    }

    // 데이터베이스에 당첨자 저장
    @Scheduled(cron = "0 23 10 * * ?")
    public void saveWinnersToDatabase() {
        Set<String> winners = template.opsForSet().members("winners");
//        Set<String> winners = template.opsForZSet().range("winners", 0, -1); // 모든 당첨자 가져오기
        System.out.println(winners);
        if (winners != null) {
            for (String winnerIdStr : winners) {
                Long winnerId = Long.valueOf(winnerIdStr);
                saveToDatabase(winnerId);
            }
        }
    }

    private void saveToDatabase(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        Event event = eventRepository.findById(1L).orElse(null);

        if (user != null && event != null) {
            JoinEvent joinEvent = JoinEvent.builder()
                    .user(user)
                    .event(event)
                    .name(user.getUsername())
                    .phoneNumber(user.getPhoneNumber())
                    .createdAt(LocalDateTime.now())
                    .isWin(true)
                    .build();

            joinEventRepository.save(joinEvent);
        }
    }

    public EventDto currentEvent() {
        LocalDateTime now = LocalDateTime.now();
        Event event = eventRepository.findFirstByStartDateBeforeAndExpiredDateAfter(now, now);
        if(event == null) throw new RuntimeException("현재 진행중인 이벤트가 없습니다.");

        return EventDto.fromEntity(event);
    }
}