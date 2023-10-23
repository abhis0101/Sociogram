package com.sociogram.server.service;

import com.sociogram.server.model.EndpointHit;
import com.sociogram.server.repository.EndpointHitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sociogram.dto.EndpointHitDto;
import com.sociogram.dto.GetStatsDto;
import com.sociogram.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.sociogram.server.mapper.EndpointHitMapper.*;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EndpointHitServiceImpl implements EndpointHitService {

    private final EndpointHitRepository endpointHitRepository;

    @Transactional
    @Override
    public EndpointHitDto saveHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = toEndpointHit(endpointHitDto);
        log.info("Save endpoint hit.");
        return toEndpointHitDto(endpointHitRepository.save(endpointHit));
    }

    @Override
    public List<ViewStatsDto> getViewStats(GetStatsDto getStatsDto) {
        if (getStatsDto == null) {
            log.error("getStatsDto is null in getViewStats method.");
            return Collections.emptyList();
        }
        LocalDateTime startDate = LocalDateTime.parse(getStatsDto.getStart(), DATE_TIME_FORMATTER);
        LocalDateTime endDate = LocalDateTime.parse(getStatsDto.getEnd(), DATE_TIME_FORMATTER);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Неверно выбран период");
        }
        List<ViewStatsDto> viewStats;
        List<String> uris = getStatsDto.getUris();

        if (uris == null || uris.isEmpty()) {
            viewStats = (getStatsDto.getUnique()
                    ? endpointHitRepository.getStatsUniqueByTime(startDate, endDate)
                    : endpointHitRepository.getAllStatsByTime(startDate, endDate));
        } else {
            viewStats = (getStatsDto.getUnique()
                    ? endpointHitRepository.getStatsUniqueByTimeAndUris(startDate, endDate, uris)
                    : endpointHitRepository.getStatsByTimeAndUris(startDate, endDate, uris));
        }
        return viewStats;
    }

}
