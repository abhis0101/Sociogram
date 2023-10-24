package com.sociogram.main_service.compilation.mapper;

import com.sociogram.main_service.compilation.dto.CompilationDto;
import com.sociogram.main_service.compilation.dto.NewCompilationDto;
import com.sociogram.main_service.compilation.dto.UpdateCompilationRequest;
import com.sociogram.main_service.compilation.model.Compilation;
import com.sociogram.main_service.event.dto.EventShortDto;
import com.sociogram.main_service.event.model.Event;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Set;

@UtilityClass
public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return Compilation
                .builder()
                .id(null)
                .title(newCompilationDto.getTitle())
                .events(null)
                .pinned(newCompilationDto.isPinned())
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> events) {
        return CompilationDto
                .builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .events(events)
                .pinned(compilation.getPinned())
                .build();
    }

    public static void fromUpdateCompilationDtoToCompilation(UpdateCompilationRequest updateCompilationRequest, Compilation compilation, Set<Event> events) {
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (events != null) {
            compilation.setEvents(events);
        }
        compilation.setPinned(updateCompilationRequest.getPinned());

    }
}
