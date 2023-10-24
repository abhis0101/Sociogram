package com.sociogram.main_service.requests.service;

import com.sociogram.main_service.requests.dto.EventParticipationRequestStatusUpdateRequestDto;
import com.sociogram.main_service.requests.dto.EventParticipationRequestStatusUpdateResponseDto;
import com.sociogram.main_service.requests.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto createParticipationRequest(Long userId, Long eventId, LocalDateTime localDateTime);

    ParticipationRequestDto cancelParticipationRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getParticipationRequest(Long userId);

    List<ParticipationRequestDto> getAllUserEventRequests(Long eventId, Long userId);

    EventParticipationRequestStatusUpdateResponseDto updateParticipationRequestsStatus(
            EventParticipationRequestStatusUpdateRequestDto updater,
            Long eventId,
            Long userId
    );
}
