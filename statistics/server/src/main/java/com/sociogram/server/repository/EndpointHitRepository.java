package com.sociogram.server.repository;

import com.sociogram.dto.ViewStatsDto;
import com.sociogram.server.model.EndpointHit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.dto.ViewStatsDto(e.app, e.uri, count(e.ip)) " +
            "from EndpointHit as e " +
            "where e.hitTime between :start and :end " +
            "and e.uri in :uris " +
            "group by e.uri, e.app " +
            "order by count(e.ip) desc ")
    List<ViewStatsDto> getStatsByTimeAndUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.dto.ViewStatsDto(e.app, e.uri, count(e.ip))" +
            "from EndpointHit as e " +
            "where e.hitTime between :start and :end " +
            "group by e.uri, e.app " +
            "order by count(e.ip) desc ")
    List<ViewStatsDto> getAllStatsByTime(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.ViewStatsDto(e.app, e.uri, count(distinct e.ip)) " +
            "from EndpointHit as e " +
            "where e.hitTime between :start and :end " +
            "and e.uri in :uris " +
            "group by e.uri, e.app " +
            "order by count(e.ip) desc ")
    List<ViewStatsDto> getStatsUniqueByTimeAndUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.dto.ViewStatsDto(e.app, e.uri, count(distinct e.ip)) " +
            "from EndpointHit as e " +
            "where e.hitTime between :start and :end " +
            "group by e.uri, e.app " +
            "order by count(e.ip) desc ")
    List<ViewStatsDto> getStatsUniqueByTime(LocalDateTime start, LocalDateTime end);

}
