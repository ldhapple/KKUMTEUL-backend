package com.kkumteul.config.scheduler;

import com.kkumteul.domain.event.service.JoinEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@EnableScheduling
@Configuration
@RequiredArgsConstructor
public class EventScheduler {

    private final JoinEventService joinEventService;

    @Scheduled(cron = "0 50 12 * * ?")
    public void setWinners() {
        joinEventService.setEventWinners();
    }
}
