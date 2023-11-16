package ru.practicum.stats.server.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.server.model.EndpointHit;

@Component
public class EndpointHitMapper {
    private EndpointHitMapper() {
    }

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        return EndpointHit.builder()
            .uri(endpointHitDto.getUri())
            .app(endpointHitDto.getApp())
            .ip(endpointHitDto.getIp())
            .timestamp(endpointHitDto.getTimestamp())
            .build();
    }
}
