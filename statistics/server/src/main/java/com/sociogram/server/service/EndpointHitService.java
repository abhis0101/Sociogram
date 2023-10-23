package com.sociogram.server.service;

import com.sociogram.dto.EndpointHitDto;
import com.sociogram.dto.GetStatsDto;
import com.sociogram.dto.ViewStatsDto;
import java.util.List;

public interface EndpointHitService {

    EndpointHitDto saveHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getViewStats(GetStatsDto getStatsDto);
}
