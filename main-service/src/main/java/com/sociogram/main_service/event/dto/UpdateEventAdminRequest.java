package com.sociogram.main_service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sociogram.main_service.event.location.model.Location;
import com.sociogram.main_service.event.model.StateAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventAdminRequest {
    private String annotation;
    private Long category;
    @Size(max = 7000)
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
    private String title;

}
