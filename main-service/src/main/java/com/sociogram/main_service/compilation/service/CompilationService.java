package com.sociogram.main_service.compilation.service;

import com.sociogram.main_service.compilation.dto.CompilationDto;
import com.sociogram.main_service.compilation.dto.NewCompilationDto;
import com.sociogram.main_service.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto createNewCompilation(NewCompilationDto newCompilationDto);

    void removeCompilation(Long id);

    CompilationDto getCompilationById(Long id);

    List<CompilationDto> getCompilationsByPinned(Boolean pinned, Integer from, Integer size);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);
}
