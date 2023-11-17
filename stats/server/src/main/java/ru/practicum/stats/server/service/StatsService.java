package ru.practicum.stats.server.service;

import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    EndpointHitDto create(EndpointHitDto endpointHitDto);

    List<ViewStatDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
