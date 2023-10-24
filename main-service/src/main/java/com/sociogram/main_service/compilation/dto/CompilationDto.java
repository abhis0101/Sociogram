package com.sociogram.main_service.compilation.dto;

import com.sociogram.main_service.event.dto.EventShortDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class CompilationDto {
    private final Long id;
    private final Boolean pinned;
    private final String title;
    private final List<EventShortDto> events;
}
