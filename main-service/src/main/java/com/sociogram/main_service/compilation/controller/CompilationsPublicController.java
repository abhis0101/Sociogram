package com.sociogram.main_service.compilation.controller;

import com.sociogram.main_service.compilation.dto.CompilationDto;
import com.sociogram.main_service.compilation.service.CompilationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class CompilationsPublicController {

    private final CompilationService compilationsService;

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        return compilationsService.getCompilationById(compId);
    }

    @GetMapping
    public List<CompilationDto> getCompilationsByPinned(@RequestParam(defaultValue = "false") Boolean pinned,
                                                        @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
                                                        @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {

        return compilationsService.getCompilationsByPinned(pinned, from, size);
    }


}
