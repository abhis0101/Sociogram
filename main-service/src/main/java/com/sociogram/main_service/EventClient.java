package com.sociogram.main_service;

import com.sociogram.client.ViewStatsClient;
import com.sociogram.dto.ViewStatsDto;
import com.sociogram.main_service.event.dto.ConfirmedEventDto;
import com.sociogram.main_service.event.dto.EventShortDto;
import com.sociogram.main_service.event.mapper.EventMapper;
import com.sociogram.main_service.event.model.Event;
import com.sociogram.main_service.event.model.RequestStatus;
import com.sociogram.main_service.requests.repository.ParticipationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventClient {
    private static final String START = "1970-01-01 00:00:00";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ParticipationRequestRepository requestRepository;
    private final ViewStatsClient viewStatsClient;


    public static String formatTimeToString(LocalDateTime time) {
        return time.format(FORMATTER);
    }

    public List<EventShortDto> makeEventShortDto(Collection<Event> events) {
        Map<String, Long> viewStatsMap = toViewStats(events);

        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);

        List<EventShortDto> eventsDto = new ArrayList<>();
        for (Event event : events) {
            Long eventId = event.getId();
            Long reqCount = confirmedRequests.get(eventId);
            Long views = viewStatsMap.get(String.format("/events/%s", eventId));
            if (reqCount == null) {
                reqCount = 0L;
            }
            if (views == null) {
                views = 0L;
            }
            eventsDto.add(
                    EventMapper.toEventDtoShort(event, reqCount, views)
            );
        }

        return eventsDto;
    }

    public Map<String, Long> toViewStats(Collection<Event> events) {
        List<String> urisToSend = new ArrayList<>();
        for (Event event : events) {
            urisToSend.add(String.format("/events/%s", event.getId()));
        }

        List<ViewStatsDto> viewStats = viewStatsClient.getStats(
                START,
                LocalDateTime.now().format(FORMATTER),
                urisToSend,
                true
        );

        return viewStats.stream()
                .collect(Collectors.toMap(ViewStatsDto::getUri, ViewStatsDto::getHits));
    }

    public Map<Long, Long> getConfirmedRequests(Collection<Event> events) {
        List<Long> eventsIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        List<ConfirmedEventDto> confirmedDtos =
                requestRepository.countConfirmedRequests(eventsIds, RequestStatus.CONFIRMED);
        return confirmedDtos.stream()
                .collect(Collectors.toMap(ConfirmedEventDto::getEventId, ConfirmedEventDto::getCount));
    }
}
