package com.sociogram.main_service.requests.service;

import com.sociogram.main_service.event.model.Event;
import com.sociogram.main_service.event.model.RequestStatus;
import com.sociogram.main_service.event.model.State;
import com.sociogram.main_service.event.repository.EventRepository;
import com.sociogram.main_service.exception.DataConflictException;
import com.sociogram.main_service.exception.EntityNotFoundException;
import com.sociogram.main_service.requests.dto.EventParticipationRequestStatusUpdateRequestDto;
import com.sociogram.main_service.requests.dto.EventParticipationRequestStatusUpdateResponseDto;
import com.sociogram.main_service.requests.dto.ParticipationRequestDto;
import com.sociogram.main_service.requests.mapper.ParticipationRequestMapper;
import com.sociogram.main_service.requests.model.ParticipationRequest;
import com.sociogram.main_service.requests.repository.ParticipationRequestRepository;
import com.sociogram.main_service.user.model.User;
import com.sociogram.main_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.sociogram.main_service.requests.mapper.ParticipationRequestMapper.toParticipationRequestDtoList;


@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepository participationRequestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId, LocalDateTime localDateTime) {
        Optional<ParticipationRequest> request = participationRequestRepository.findByEventIdAndRequesterId(eventId, userId);
        if (request.isPresent()) {
            throw new DataConflictException("The request to participate has already been sent");
        }

        User requester = getUser(userId);
        Event event = getEvent(eventId);

        if (userId.equals(event.getInitiator().getId())) {
            throw new DataConflictException("Request to participate has already been sent");
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new DataConflictException("Failed to publish event");
        }
        int limit = event.getParticipantLimit();
        if (limit != 0) {
            Long numberOfRequests = participationRequestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            if (numberOfRequests >= limit) {
                throw new DataConflictException("Too many event participants");
            }
        }

        ParticipationRequest participationRequest = ParticipationRequest
                .builder()
                .created(localDateTime)
                .requester(requester)
                .event(event)
                .status(RequestStatus.PENDING)
                .build();
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            participationRequest.setStatus(RequestStatus.CONFIRMED);
        }
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequestRepository.save(participationRequest));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        ParticipationRequest participationRequest = participationRequestRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException("Request not found"));
        if (participationRequest.getRequester().getId().equals(userId)) {
            participationRequest.setStatus(RequestStatus.CANCELED);
            return ParticipationRequestMapper.toParticipationRequestDto(participationRequestRepository.save(participationRequest));
        }
        throw new DataConflictException("Another person's request cannot be cancelled");

    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getParticipationRequest(Long userId) {
        log.info("Information about user requests received.");
        return toParticipationRequestDtoList(participationRequestRepository.findAllByRequesterId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllUserEventRequests(Long eventId, Long userId) {
        getUser(userId);
        Event event = getEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new DataConflictException("A user cannot view requests for an event that they are not the author of");
        }
        log.info("Received information about user event participation requests.");
        return toParticipationRequestDtoList(participationRequestRepository.findAllByEventId(eventId));
    }

    @Override
    @Transactional
    public EventParticipationRequestStatusUpdateResponseDto updateParticipationRequestsStatus(
            EventParticipationRequestStatusUpdateRequestDto updater,
            Long eventId,
            Long userId) {
        RequestStatus status = updater.getStatus();
        if (status == RequestStatus.CONFIRMED || status == RequestStatus.REJECTED) {
            getUser(userId);
            Event event = getEvent(eventId);
            if (!event.getInitiator().getId().equals(userId)) {
                throw new DataConflictException("A user cannot update requests for an event that they are not the author of");
            }
            Integer participantLimit = event.getParticipantLimit();
            if (!event.getRequestModeration() || participantLimit == 0) {
                throw new DataConflictException("The event does not need moderation");
            }
            Long numberOfParticipants = participationRequestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            if (numberOfParticipants >= participantLimit) {
                throw new DataConflictException("The event already has a maximum number of participants");
            }
            List<ParticipationRequest> requests = participationRequestRepository.findAllByIdIn(updater.getRequestIds());
            RequestStatus newStatus = updater.getStatus();
            for (ParticipationRequest request : requests) {
                if (request.getEvent().getId().equals(eventId)) {
                    if (participantLimit > numberOfParticipants) {
                        if (newStatus == RequestStatus.CONFIRMED && request.getStatus() != RequestStatus.CONFIRMED) {
                            numberOfParticipants++;
                        }
                        request.setStatus(newStatus);
                    } else {
                        request.setStatus(RequestStatus.REJECTED);
                    }
                } else {
                    throw new DataConflictException("Request and event do not match");
                }
            }
            requests = participationRequestRepository.saveAll(requests);
            List<ParticipationRequestDto> confirmedRequestDtos = toParticipationRequestDtoList(participationRequestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED));

            List<ParticipationRequestDto> rejectedRequestDtos = toParticipationRequestDtoList(participationRequestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.REJECTED));

            return new EventParticipationRequestStatusUpdateResponseDto(confirmedRequestDtos, rejectedRequestDtos);
        } else {
            throw new IllegalArgumentException("Only CONFIRMED or REJECTED statuses are available");
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Invalid user ID."));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Invalid event ID."));
    }

}
