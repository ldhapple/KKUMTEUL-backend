package com.kkumteul.util.redis;

import com.kkumteul.domain.event.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {
    private final TicketService ticketService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String topic = new String(pattern);
        String msg = new String(message.getBody());

        if ("eventStartTopic".equals(topic)) {
            ticketService.handleEventStartMessage(msg);
        } else if ("ticketRemainTopic".equals(topic)) {
            ticketService.handleTicketRemainMessage(msg);
        }
    }
}
