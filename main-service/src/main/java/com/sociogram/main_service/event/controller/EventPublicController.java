package com.sociogram.main_service.event.controller;

import com.sociogram.main_service.event.dto.EventFullDto;
import com.sociogram.main_service.event.dto.EventShortDto;
import com.sociogram.main_service.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventPublicController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(
            HttpServletRequest request,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false) Boolean onlyAvailable,
            @RequestParam(required = false) String sort
    ) {
        return eventService.getEventsPublicController(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                request.getRemoteAddr(), request.getRequestURI(), from, size);
    }

    @GetMapping(path = "/{eventId}")
    public EventFullDto getEvent(@PathVariable("eventId") Long eventId, HttpServletRequest request) {
        return eventService.getEventByIdPublic(eventId, request.getRemoteAddr(), request.getRequestURI());
    }
}
