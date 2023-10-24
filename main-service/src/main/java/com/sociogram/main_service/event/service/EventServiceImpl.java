package com.sociogram.main_service.event.service;

import com.sociogram.client.ViewStatsClient;
import com.sociogram.dto.EndpointHitDto;
import com.sociogram.dto.ViewStatsDto;
import com.sociogram.main_service.EventClient;
import com.sociogram.main_service.category.model.Category;
import com.sociogram.main_service.category.repository.CategoryRepository;
import com.sociogram.main_service.event.dto.*;
import com.sociogram.main_service.event.mapper.EventMapper;
import com.sociogram.main_service.event.model.Event;
import com.sociogram.main_service.event.model.RequestStatus;
import com.sociogram.main_service.event.model.State;
import com.sociogram.main_service.event.model.StateAction;
import com.sociogram.main_service.event.repository.EventRepository;
import com.sociogram.main_service.exception.DataConflictException;
import com.sociogram.main_service.exception.InvalidStatusException;
import com.sociogram.main_service.requests.model.ParticipationRequest;
import com.sociogram.main_service.requests.repository.ParticipationRequestRepository;
import com.sociogram.main_service.user.model.User;
import com.sociogram.main_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final ViewStatsClient viewStatsClient;
    private static final String APP = "ewm-main-service";
    private static final String LOWER_DATE = "1970-01-01 00:00:00";
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventClient eventClient;
    private final ParticipationRequestRepository participationRequestRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventByUserId(Long id, int from, int size) {
        int page = 0;
        if (from != 0) {
            page = from / size;
        }
        List<Event> events = eventRepository.findAllByInitiatorId(id, PageRequest.of(page, size));
        List<EventFullDto> eventFullDtos = new ArrayList<>();
        for (Event event : events) {
            eventFullDtos.add(EventMapper.toEventFullDto(event, 0L, 0L));
        }
        return eventFullDtos;
    }

    @Override
    @Transactional
    public EventFullDto createNewEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getDescription().isBlank()) {
            throw new InvalidStatusException("Description cannot be empty");
        }
        User user = getUser(userId);
        Category category = getCategory(newEventDto.getCategory());
        if (newEventDto.getPaid() == null) {
            newEventDto.setPaid(false);
        }
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new IllegalArgumentException("The event date has already arrived");
        }
        Event event = EventMapper.toEvent(newEventDto, user, category, State.PENDING);
        eventRepository.save(event);
        log.info("Event saved.");
        return EventMapper.toEventFullDto(event, 0L, 0L);
    }

    @Override
    public EventFullDto getFullInfoByUserIdAndEventId(Long userId, Long eventId) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long eventId) {
        Event event = getEvent(eventId);
        return EventMapper.toEventFullDto(event, 0L, 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getFullEventInfoByParam(List<Long> users, List<Long> categories, List<State> states,
                                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().minusYears(4000);
        }

        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(10000);
        }
        Specification<Event> eventSpecification = Specification.where(inEventDates(rangeStart, rangeEnd))
                .and(inCategoryIds(categories))
                .and(inStates(states))
                .and(inUserIds(users));

        Sort sort = Sort.by(
                Sort.Order.desc("eventDate"),
                Sort.Order.asc("createdOn"));


        List<Event> events = eventRepository.findAll(eventSpecification, PageRequest.of(
                from / size,
                size,
                sort)).getContent();

        if (events.isEmpty()) {
            return new ArrayList<>();
        }
        return makeEventDtos(events);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = getEvent(eventId);
        if (updateEventAdminRequest.getStateAction() != null) {
            if (event.getState() != State.PENDING) {
                throw new DataConflictException("Invalid event status");
            } else if (
                    event.getEventDate().isBefore(LocalDateTime.now().plusHours(1)) && updateEventAdminRequest.getStateAction() == StateAction.PUBLISH_EVENT
            ) {
                throw new DataConflictException("Unable to post, less than an hour left before the event starts");
            }
        }
        if (updateEventAdminRequest.getEventDate() != null) {

            if (updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("The date has already arrived");
            }
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(updateEventAdminRequest.getLocation());
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction() == StateAction.CANCEL_REVIEW) {
                event.setState(State.CANCELED);
            }
            if (updateEventAdminRequest.getStateAction() == StateAction.PUBLISH_EVENT) {
                event.setState(State.PUBLISHED);
            }
            if (updateEventAdminRequest.getStateAction() == StateAction.REJECT_EVENT) {
                event.setState(State.REJECTED);
            }
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            Category category = getCategory(updateEventAdminRequest.getCategory());
            event.setCategory(category);
        }
        eventRepository.save(event);
        return EventMapper.toEventFullDto(event, 0L, 0L);
    }

    @Override
    public NewEventDto updateEventCurrentUser(Long userId, Long eventId, NewEventDto newEventDto) {
        return null;
    }

    @Override
    public NewEventDto getInfoEventByCurrentUser(Long userId, Long eventId) {
        return null;
    }

    @Override
    public NewEventDto updateStatusEvent(Long userId, Long eventId) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsPublicController(String text, List<Long> categoryIds, Boolean paid, LocalDateTime start,
                                                         LocalDateTime end, Boolean onlyAvailable, String sort, String ip, String uri, Integer from, Integer size
    ) {
        viewStatsClient.addHit(new EndpointHitDto(
                APP,
                uri,
                ip,
                EventClient.formatTimeToString(LocalDateTime.now())
        ));
        if (start == null) {
            start = LocalDateTime.now();
        }
        if (end == null) {
            end = LocalDateTime.now().plusYears(10000);
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("The time indicated is incorrect");
        }
        Specification<Event> spec = Specification.where(inStates(List.of(State.PUBLISHED)))
                .and(inEventDates(start, end))
                .and(inCategoryIds(categoryIds))
                .and(getPaid(paid))
                .and(annotationAndDescription(text));

        if (onlyAvailable != null && onlyAvailable) {
            spec = spec.and(byParticipantLimit());
        }
        PageRequest pageRequest = PageRequest.of(
                from / size,
                size,
                Sort.by(Sort.Direction.DESC, "eventDate"));
        List<Event> events = eventRepository.findAll(spec, pageRequest).getContent();
        if (events.isEmpty()) {
            return new ArrayList<>();
        }
        List<EventShortDto> eventShortDtos = eventClient.makeEventShortDto(events);
        if (Objects.equals(sort, "VIEWS")) {
            eventShortDtos = eventShortDtos.stream()
                    .sorted(Comparator.comparing(EventShortDto::getViews).reversed())
                    .collect(Collectors.toList());
        }
        return eventShortDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByIdPublic(Long eventId, String ip, String uri) {
        Event event = getEvent(eventId);
        if (event.getState() != State.PUBLISHED) {
            throw new EntityNotFoundException("Event not published");
        }
        Long confirmedRequests = participationRequestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        viewStatsClient.addHit(new EndpointHitDto(
                APP,
                uri,
                ip,
                EventClient.formatTimeToString(LocalDateTime.now())
        ));
        Long views = getViewsForOneEvent(eventId);
        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByIdByInitiator(Long eventId, Long userId) {
        Event event = getEvent(eventId);
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User is not found");
        }
        User initiator = event.getInitiator();
        if (!initiator.getId().equals(userId)) {
            throw new IllegalArgumentException("The user is not the author of the event");
        }
        Long confirmedRequests = participationRequestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        Long views = getViewsForOneEvent(eventId);
        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(EventUpdateRequestDto updateEventDto, Long eventId, Long userId) {
        LocalDateTime eventDate = updateEventDto.getEventDate();
        LocalDateTime timeCriteria = LocalDateTime.now().plusHours(2L);
        if (eventDate != null && eventDate.isBefore(timeCriteria)) {
            throw new InvalidStatusException("incorrect time");
        }
        Event event = getEvent(eventId);
        Long newCategoryId = updateEventDto.getCategory();
        Category oldCategory = event.getCategory();
        Category newCategory = oldCategory;
        if (newCategoryId != null) {
            if (oldCategory == null || !oldCategory.getId().equals(newCategoryId)) {
                newCategory = getCategory(newCategoryId);
            }
        }

        User initiator = event.getInitiator();
        if (!Objects.equals(initiator.getId(), userId)) {
            throw new DataConflictException("You need to be the author of the event");
        }

        if (eventDate != null && eventDate.isBefore(timeCriteria)) {
            throw new DataConflictException("Incorrect time");
        }
        if (event.getState() == State.PUBLISHED) {
            throw new DataConflictException("Invalid event status");
        }

        State newState = event.getState();
        StateAction action = updateEventDto.getStateAction();
        if (action != null) {
            switch (action) {
                case SEND_TO_REVIEW:
                    newState = State.PENDING;
                    break;
                case CANCEL_REVIEW:
                    newState = State.CANCELED;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid status");
            }
        }
       event =  EventMapper.forUpdateDto(updateEventDto, newCategory, newState, event);

        return EventMapper.toEventFullDto(event, 0L, 0L);
    }

    private Long getViewsForOneEvent(Long eventId) {
        List<String> urisToSend = List.of(String.format("/events/%s", eventId));
        List<ViewStatsDto> viewStats = viewStatsClient.getStats(
                LOWER_DATE,
                EventClient.formatTimeToString(LocalDateTime.now()),
                urisToSend,
                true
        );
        ViewStatsDto viewStatsDto = viewStats == null || viewStats.isEmpty() ? null : viewStats.get(0);
        return viewStatsDto == null || viewStatsDto.getHits() == null ? 0 : viewStatsDto.getHits();
    }

    private Specification<Event> getPaid(Boolean paid) {
        return paid == null ? null : (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paid"), paid);
    }

    private Specification<Event> annotationAndDescription(String text) {
        return text == null ? null : (root, query, criteriaBuilder) -> {
            String lowerCasedText = text.toLowerCase();
            Expression<String> annotation = criteriaBuilder.lower(root.get("annotation"));
            Expression<String> description = criteriaBuilder.lower(root.get("description"));
            return criteriaBuilder.or(
                    criteriaBuilder.like(annotation, "%" + lowerCasedText + "%"),
                    criteriaBuilder.like(description, "%" + lowerCasedText + "%")
            );
        };
    }

    private Specification<Event> byParticipantLimit() {
        return (root, query, criteriaBuilder) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<ParticipationRequest> subRoot = sub.from(ParticipationRequest.class);
            sub.select(criteriaBuilder.count(subRoot.get("id"))).where(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(subRoot.get("status"), RequestStatus.CONFIRMED),
                            criteriaBuilder.equal(subRoot.get("event").get("id"), root.get("id"))
                    )
            );
            return criteriaBuilder.greaterThan(root.get("participantLimit"), sub);
        };
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Invalid user ID."));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Invalid event ID."));
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new EntityNotFoundException("Invalid category ID."));
    }

    private Specification<Event> inUserIds(List<Long> users) {
        return users == null ? null : (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get("initiator").get("id")).value(users);
    }

    private Specification<Event> inCategoryIds(List<Long> categories) {
        return categories == null ? null : (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get("category").get("id")).value(categories);
    }

    private Specification<Event> inStates(List<State> states) {
        return states == null ? null : (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get("state")).value(states);
    }

    private Specification<Event> inEventDates(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        return rangeStart == null || rangeEnd == null ? null : (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("eventDate"), rangeStart, rangeEnd);
    }


    private List<EventFullDto> makeEventDtos(List<Event> events) {
        Map<String, Long> viewStatsMap = eventClient.toViewStats(events);

        Map<Long, Long> confirmedRequests = eventClient.getConfirmedRequests(events);

        List<EventFullDto> eventsDto = new ArrayList<>();
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
                    EventMapper.toEventFullDto(event, reqCount, views)
            );
        }

        return eventsDto;
    }
}
