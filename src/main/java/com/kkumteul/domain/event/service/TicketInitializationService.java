// TicketInitializationService.java
package com.kkumteul.domain.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketInitializationService {
    private final RedisTemplate<String, String> template;
    private final ChannelTopic eventStartTopic;
    private final ChannelTopic ticketRemainTopic;

    @Scheduled(cron = "0 0 12 * * ?")
    public void initializeTickets() {
        String ticketListKey = "tickets";
        template.delete(ticketListKey);

        for (int i = 1; i <= 100; i++) {
            template.opsForList().rightPush(ticketListKey, "ticket-" + i);
        }

        template.delete("join_user");
        template.delete("winners");
        template.delete("active_tickets");

        template.convertAndSend(eventStartTopic.getTopic(), "true");
        template.convertAndSend(ticketRemainTopic.getTopic(), "true");

        log.info("티켓 리스트가 초기화되었습니다.");
    }

    @Scheduled(cron = "0 0 13 * * ?")
    public void activateEvent() {
        template.convertAndSend(eventStartTopic.getTopic(), "true");
    }
}
