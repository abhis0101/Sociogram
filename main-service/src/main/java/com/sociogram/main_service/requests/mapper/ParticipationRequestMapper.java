package com.sociogram.main_service.requests.mapper;

import com.sociogram.main_service.requests.dto.ParticipationRequestDto;
import com.sociogram.main_service.requests.model.ParticipationRequest;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ParticipationRequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto
                .builder()
                .requester(participationRequest.getRequester().getId())
                .id(participationRequest.getId())
                .created(participationRequest.getCreated())
                .event(participationRequest.getEvent().getId())
                .status(participationRequest.getStatus())
                .build();
    }

    public static List<ParticipationRequestDto> toParticipationRequestDtoList(List<ParticipationRequest> requests) {
        return requests.stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

}
