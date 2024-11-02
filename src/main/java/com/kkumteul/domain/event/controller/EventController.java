package com.kkumteul.domain.event.controller;


import com.kkumteul.auth.dto.CustomUserDetails;
import com.kkumteul.domain.event.dto.EventDto;
import com.kkumteul.domain.event.dto.EventResultResponseDto;
import com.kkumteul.domain.event.dto.JoinEventRequestDto;
import com.kkumteul.domain.event.service.EventService;
import com.kkumteul.domain.event.service.JoinEventService;
import com.kkumteul.domain.event.service.TicketInitializationService;
import com.kkumteul.domain.event.service.TicketService;
import com.kkumteul.util.ApiUtil;
import com.kkumteul.util.ApiUtil.ApiSuccess;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    private final TicketInitializationService ticketInitializationService;
    private final JoinEventService joinEventService;

    @PostMapping("{userId}")
    public ApiSuccess<?> joinEvent(@PathVariable(name = "userId") Long userId) {
        // TODO: userId JWT 방식으로 변경
//        Long userId = 1L;
        String applyTicketResult = eventService.insertJoinEvent(userId);
        return ApiUtil.success(applyTicketResult);
    }

    @PostMapping("")
    public ApiSuccess<?> joinEvent(@AuthenticationPrincipal CustomUserDetails user) {
        // TODO: userId JWT 방식으로 변경
        Long userId = user.getId();
        String applyTicketResult = eventService.insertJoinEvent(userId);
        return ApiUtil.success(applyTicketResult);
    }

    // 이름, 전화번호 입력 후 처리 메서드 (JoinEventService 에서 처리)
    @PostMapping("/register")
    public ApiSuccess<?> registerEvent(@AuthenticationPrincipal CustomUserDetails user,@RequestBody JoinEventRequestDto joinEventRequestDto) {
        String result = joinEventService.joinEvent(user.getId(), joinEventRequestDto);
        return ApiUtil.success(result);
    }

    @PostMapping("/init")
    public ApiSuccess<?> initEvent() {
        ticketInitializationService.initializeTickets();
        ticketInitializationService.activateEvent();
        return ApiUtil.success("이벤트 시작 전 초기화 성공");
    }

    // 특정 유저의 이벤트 참여 결과 리스트
//    @GetMapping("{userId}")
//    public ApiSuccess<?> getJoinEventResults(@PathVariable(name = "userId") Long userId) {
//        // TODO: userId JWT 방식으로 변경
//        List<EventResultResponseDto> joinEventResult = eventService.getJoinEventResults(userId);
//        return ApiUtil.success(joinEventResult);
//    }

//    @GetMapping("/test")
//    public ApiSuccess<?> joinEventResult() {
//
//        eventService.saveWinnersToDatabase();
//        return ApiUtil.success("joined event successfully");
//    }

    @GetMapping("")
    public ApiSuccess<?> currentEvent() {
        EventDto eventDto = eventService.currentEvent();
        return ApiUtil.success(eventDto);

    }

    @GetMapping("/result")
    public ApiSuccess<?> getEventResult() {
        List<EventResultResponseDto> yesterdayEventResults = joinEventService.getYesterdayEventResults();
        return ApiUtil.success(yesterdayEventResults);
    }


}
