package com.sociogram.server.controller;

import com.sociogram.dto.EndpointHitDto;
import com.sociogram.dto.GetStatsDto;
import com.sociogram.dto.ViewStatsDto;
import com.sociogram.server.service.EndpointHitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final EndpointHitService endpointHitService;

    @PostMapping("/hit")
    @ResponseStatus(value = HttpStatus.CREATED)
    public EndpointHitDto saveHit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        return endpointHitService.saveHit(endpointHitDto);
    }

    @GetMapping("stats")
    public List<ViewStatsDto> getViewStats(@RequestParam String start,
                                           @RequestParam String end,
                                           @RequestParam(required = false) List<String> uris,
                                           @RequestParam(defaultValue = "false") Boolean unique) {
        return endpointHitService.getViewStats(new GetStatsDto(start, end, uris, unique));
    }
}
