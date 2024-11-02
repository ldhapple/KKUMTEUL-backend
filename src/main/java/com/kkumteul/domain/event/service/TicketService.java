package com.kkumteul.domain.event.service;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {
    private final RedisTemplate<String, String> template;
    private final ChannelTopic eventStartTopic;
    private final ChannelTopic ticketRemainTopic;
    private AtomicBoolean eventStartFlagCached = new AtomicBoolean(false);
    private AtomicBoolean ticketRemainFlagCached = new AtomicBoolean(true);

    // 티켓 발급 처리 및 중복 검사
    public String issueTicket(Long userId) {
        if (!eventStartFlagCached.get()) {
            return "이벤트가 아직 시작되지 않았습니다~!";
        }
        if (!ticketRemainFlagCached.get()) {
            return "남은 티켓이 없습니다~!";
        }

        String luaScript = """
            local isJoinedUser = redis.call('SISMEMBER', ARGV[1], ARGV[2])
            if isJoinedUser == 1 then
                return "이미 이벤트에 참여한 유저입니다!!"
            end

            local ticket = redis.call('LPOP', ARGV[3])
            if not ticket then
                return "남은 티켓이 없습니다!"
            end

            redis.call('SADD', ARGV[1], ARGV[2])

            return ticket
        """;

        String[] args = {
                "join_user",
                userId.toString(),
                "tickets"
        };

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

            if (resultBytes != null) {
                String resultStr = new String(resultBytes, StandardCharsets.UTF_8);
                if ("남은 티켓이 없습니다!".equals(resultStr)) {
                    if (ticketRemainFlagCached.compareAndSet(true, false)) {
                        template.convertAndSend(ticketRemainTopic.getTopic(), "false");
                    }
                } else {
                    template.expire(resultStr, 10, TimeUnit.MINUTES); //TTL 5분 설정
                    template.opsForSet().add("active_tickets", resultStr);
                    log.info("티켓이 발급되었습니다. 10분 제한 tickets: {}", resultStr);
                }
                return resultStr;
            }
            return null;
        });

        return result;
    }

    @EventListener
    public void handleEventStartMessage(String message) {
        this.eventStartFlagCached.set("true".equals(message));
    }

    @EventListener
    public void handleTicketRemainMessage(String message) {
        this.ticketRemainFlagCached.set("true".equals(message));
    }
}
