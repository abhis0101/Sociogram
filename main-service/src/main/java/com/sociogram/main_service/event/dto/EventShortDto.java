package com.sociogram.main_service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sociogram.main_service.category.dto.CategoryDto;
import com.sociogram.main_service.user.dto.UserShortDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@ToString
@Builder
public class EventShortDto {
    private final Long id;
    private final String annotation;
    private final CategoryDto category;
    private final Long confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private final LocalDateTime eventDate;
    private final UserShortDto initiator;
    private final Boolean paid;
    private final String title;
    private final Long views;
}
