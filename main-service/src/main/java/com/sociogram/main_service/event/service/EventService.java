package com.sociogram.main_service.event.service;

import com.sociogram.main_service.event.dto.*;
import com.sociogram.main_service.event.model.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> getEventByUserId(Long id, int from, int size);

    EventFullDto createNewEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getFullInfoByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto getEventById(Long eventId);

    List<EventFullDto> getFullEventInfoByParam(List<Long> users, List<Long> categories, List<State> states,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    NewEventDto updateEventCurrentUser(Long userId, Long eventId, NewEventDto newEventDto);

    NewEventDto getInfoEventByCurrentUser(Long userId, Long eventId);

    NewEventDto updateStatusEvent(Long userId, Long eventId);

    EventFullDto getEventByIdPublic(Long eventId, String ip, String uri);

    List<EventShortDto> getEventsPublicController(String text, List<Long> categoryIds, Boolean paid, LocalDateTime start,
                                                  LocalDateTime end, Boolean onlyAvailable, String sort, String ip, String uri, Integer from, Integer size);

    EventFullDto getEventByIdByInitiator(Long eventId, Long userId);

    EventFullDto updateEventByUser(EventUpdateRequestDto updateEventDto, Long eventId, Long userId);
}
