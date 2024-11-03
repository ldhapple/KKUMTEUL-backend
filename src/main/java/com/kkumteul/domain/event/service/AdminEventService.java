package com.kkumteul.domain.event.service;

import com.kkumteul.domain.event.dto.EventDto;
import com.kkumteul.domain.event.entity.Event;
import com.kkumteul.domain.event.repository.EventRepository;
import com.kkumteul.exception.AdminEventNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminEventService {

    private final EventRepository eventRepository;

    // insertEvent
    public void insertEvent(EventDto eventDto) {
        Event event = Event.builder()
                .name(eventDto.getEventName())
                .description(eventDto.getEventDescription())
                .startDate(eventDto.getStartDate())
                .expiredDate(eventDto.getExpiredDate())
                .build();

        eventRepository.save(event);
    }

    // updateEvent
    public void updateEvent(Long eventId, EventDto eventDto) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new AdminEventNotFoundException(eventDto.getEventId().toString()));

        event.update(eventDto.getEventName(), eventDto.getEventDescription(), eventDto.getStartDate(), eventDto.getExpiredDate());
    }

    // deleteEvent
    public void deleteEvent(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new AdminEventNotFoundException(eventId.toString()));

        eventRepository.deleteById(eventId);
    }

    // getEventDetail
    public EventDto getEventDetail(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new AdminEventNotFoundException(eventId.toString()));

        return EventDto.fromEntity(event);
    }

    // getEventList
    public Page<EventDto> getEventList(final Pageable pageable) {
        Page<Event> eventList = eventRepository.findAllEventInfo(pageable);

        return eventList.map(EventDto::fromEntity);
    }
}
