package com.kkumteul.domain.event.controller;


import com.kkumteul.domain.event.dto.EventDto;
import com.kkumteul.domain.event.dto.EventRequestDto;
import com.kkumteul.domain.event.dto.EventResultResponseDto;
import com.kkumteul.domain.event.service.EventService;
import com.kkumteul.util.ApiUtil;
import com.kkumteul.util.ApiUtil.ApiSuccess;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    @PostMapping("{userId}")
    public ApiSuccess<?> joinEvent(@PathVariable(name = "userId") Long userId, @RequestBody EventRequestDto eventRequestDto) {
        // TODO: userId JWT 방식으로 변경
//        Long userId = 1L;
        eventService.insertJoinEvent(userId, eventRequestDto);
        return ApiUtil.success("joined event successfully");
    }

    // 특정 유저의 이벤트 참여 결과 리스트
//    @GetMapping("{userId}")
//    public ApiSuccess<?> getJoinEventResults(@PathVariable(name = "userId") Long userId) {
//        // TODO: userId JWT 방식으로 변경
//        List<EventResultResponseDto> joinEventResult = eventService.getJoinEventResults(userId);
//        return ApiUtil.success(joinEventResult);
//    }

    @GetMapping("/test")
    public ApiSuccess<?> joinEventResult() {

        eventService.saveWinnersToDatabase();
        return ApiUtil.success("joined event successfully");
    }

    @GetMapping("")
    public ApiSuccess<?> currentEvent() {
        EventDto eventDto = eventService.currentEvent();
        return ApiUtil.success(eventDto);

    }

}
