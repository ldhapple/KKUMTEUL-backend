package com.kkumteul.domain.event.service;

import com.kkumteul.domain.event.dto.EventRequestDto;
import com.kkumteul.domain.event.entity.Event;
import com.kkumteul.domain.event.entity.JoinEvent;
import com.kkumteul.domain.event.repository.EventRepository;
import com.kkumteul.domain.event.repository.JoinEventRepository;
import com.kkumteul.domain.user.entity.User;
import com.kkumteul.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
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
    private final RedissonClient redissonClient; // RedissonClient 추가

    private static final int QUEUE_LIMIT = 100; // 대기열 크기
    private static final int ISSUE_BATCH_SIZE = 10; // 한 번에 발급할 쿠폰 수

    @Transactional
    public void insertJoinEvent(Long userId, EventRequestDto eventRequestDto) {
        // Redisson 락 추가
        RLock lock = redissonClient.getLock("eventLock_" + eventRequestDto.getEventId());
        String luaScript = """
            local eventId = ARGV[1]
            local userId = ARGV[2]
            local uniqueKey = ARGV[3]
            local queueLimit = tonumber(ARGV[4])
            local score = tonumber(ARGV[5])
            local eventKey = 'event' .. eventId
            local duplicatesKey = eventKey .. ':duplicates'
            local winnersKey = 'winners'
            local issuedJoinEventCount = tonumber(redis.call('GET', 'issuedJoinEvent') or '0') -- nil이면 '0'으로
              
            if issuedJoinEventCount >= queueLimit then
                return 'QUEUE_FULL'
            end

            if redis.call('SISMEMBER', duplicatesKey, uniqueKey) == 1 then
                return 'DUPLICATE_REQUEST'
            end

            if redis.call('ZSCORE', eventKey, userId) ~= false then
                return 'DUPLICATE_USER_ID'
            end

            redis.call('ZADD', eventKey, score, userId) -- ZSet에 데이터 추가
            redis.call('SADD', duplicatesKey, uniqueKey)  -- Set에 데이터 추가
            redis.call('INCR', 'issuedJoinEvent')
            return 'SUCCESS'
            """;

        Long eventId = eventRequestDto.getEventId();
        String uniqueKey = eventRequestDto.getPhoneNumber() + eventRequestDto.getUsername();
        long score = System.currentTimeMillis(); // 타임스탬프를 Java에서 생성
        String[] args = {String.valueOf(eventId), String.valueOf(userId), uniqueKey, String.valueOf(QUEUE_LIMIT),
                String.valueOf(score)};

        // Lua 스크립트 실행
        String result = template.execute((RedisCallback<String>) redisConnection -> {
            byte[][] argsBytes = new byte[args.length][];
            for (int i = 0; i < args.length; i++) {
                argsBytes[i] = args[i].getBytes(StandardCharsets.UTF_8);
            }

            byte[] resultBytes = redisConnection.eval(
                    luaScript.getBytes(StandardCharsets.UTF_8),
                    ReturnType.VALUE,
                    0,
                    argsBytes
            );

            return resultBytes != null ? new String(resultBytes, StandardCharsets.UTF_8) : null;
        });

        // 분산 락으로 `QUEUE_FULL` 상태에서만 락을 걸고 처리
        if ("QUEUE_FULL".equals(result)) {
            try {
                if (lock.tryLock()) { // 락을 획득한 경우에만 실행
                    processQueue(eventId); // `processQueue`로 대기열 처리
                }
            } catch (Exception e) {
                log.error("QUEUE_FULL 상태에서 processQueue 처리 실패", e);
            } finally {
                // 현재 스레드가 락을 가지고 있는 경우에만 unlock 호출
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            switch (Objects.requireNonNull(result)) {
                case "DUPLICATE_REQUEST":
                    log.info("이미 발급 요청이 있습니다 (phoneNumber & username): {}", uniqueKey);
                    break;
                case "DUPLICATE_USER_ID":
                    log.info("이미 발급 요청이 있습니다 (userId): {}", userId);
                    break;
                case "SUCCESS":
                    log.info("요청이 접수되었습니다. userId: {}", userId);
                    break;
            }
        }
    }

    // `processQueue` 메서드는 그대로 사용합니다.
    public void processQueue(Long eventId) {
        String eventKey = "event" + eventId;
        Set<String> usersId = template.opsForZSet().range(eventKey, 0, QUEUE_LIMIT - 1);

        if (usersId != null && !usersId.isEmpty()) {
            for (String userIdStr : usersId) {
                Long userId = Long.valueOf(userIdStr);
                template.opsForSet().add("winners", String.valueOf(userId)); // Set에 추가
                log.info("쿠폰이 성공적으로 발급되었습니다. userId: {}", userId);
                template.opsForZSet().remove(eventKey, userIdStr); // 대기열에서 삭제
            }
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
}