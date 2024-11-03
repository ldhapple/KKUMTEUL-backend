package com.kkumteul.domain.event.controller;

import com.kkumteul.domain.event.dto.EventDto;
import com.kkumteul.domain.event.entity.Event;
import com.kkumteul.domain.event.service.AdminEventService;
import com.kkumteul.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/events")
public class AdminEventController {

    private final AdminEventService adminEventService;

    // 1. 이벤트 등록
    @PostMapping
    public ApiUtil.ApiSuccess<?> insertEvent(@RequestBody EventDto eventDto) {
        adminEventService.insertEvent(eventDto);

        return ApiUtil.success("event insert successfully");
    }

    // 2. 이벤트 수정
    @PutMapping("/{eventId}")
    public ApiUtil.ApiSuccess<?> updatetEvent(@RequestBody EventDto eventDto){
        adminEventService.updateEvent(eventDto);

        return ApiUtil.success("event update successfully");
    }

    // 3. 이벤트 삭제
    @DeleteMapping("/{eventId}")
    public ApiUtil.ApiSuccess<?> deleteEvent(@PathVariable long eventId) {
        adminEventService.deleteEvent(eventId);

        return ApiUtil.success("event delete successfully");
    }

    // 4. 이벤트 상세 조회
    @GetMapping("/{eventId}")
    public ApiUtil.ApiSuccess<?> getEventDetail(@PathVariable long eventId){
        EventDto event = adminEventService.getEventDetail(eventId);

        return ApiUtil.success(event);
    }

    // 5. 이벤트 목록 조회
    @GetMapping
    public ApiUtil.ApiSuccess<?> getEventList(final Pageable pageable){
        Page<EventDto> eventList = adminEventService.getEventList(pageable);

        return ApiUtil.success(eventList);
    }
}
