package com.sociogram.main_service.requests.dto;

import com.sociogram.main_service.event.model.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class EventParticipationRequestStatusUpdateRequestDto {
    @NotNull
    private final RequestStatus status;
    private final List<Long> requestIds;
}
