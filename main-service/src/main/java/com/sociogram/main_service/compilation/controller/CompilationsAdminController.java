package com.sociogram.main_service.compilation.controller;

import com.sociogram.main_service.compilation.dto.CompilationDto;
import com.sociogram.main_service.compilation.dto.NewCompilationDto;
import com.sociogram.main_service.compilation.dto.UpdateCompilationRequest;
import com.sociogram.main_service.compilation.service.CompilationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationsAdminController {

    private final CompilationService compilationsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody NewCompilationDto newCompilationDto) {
        return compilationsService.createNewCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        compilationsService.removeCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return compilationsService.updateCompilation(compId, updateCompilationRequest);
    }
}
