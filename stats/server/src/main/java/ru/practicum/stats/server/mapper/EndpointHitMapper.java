package ru.practicum.stats.server.mapper;

import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.server.model.StatsHit;

public class EndpointHitMapper {
    private EndpointHitMapper() {
    }

    public static StatsHit toStatsHit(EndpointHitDto endpointHitDto) {
        return StatsHit.builder()
            .uri(endpointHitDto.getUri())
            .app(endpointHitDto.getApp())
            .ip(endpointHitDto.getIp())
            .timestamp(endpointHitDto.getTimestamp())
            .build();
    }

    public static EndpointHitDto toDto(StatsHit statsHit) {
        return EndpointHitDto.builder()
            .app(statsHit.getApp())
            .ip(statsHit.getIp())
            .uri(statsHit.getUri())
            .timestamp(statsHit.getTimestamp())
            .build();
    }
}
