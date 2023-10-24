package com.sociogram.main_service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sociogram.main_service.event.location.model.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class NewEventDto {
    private String annotation;
    private Long category;
    @NotBlank
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private int participantLimit;
    private Boolean requestModeration;
    private String title;
}
