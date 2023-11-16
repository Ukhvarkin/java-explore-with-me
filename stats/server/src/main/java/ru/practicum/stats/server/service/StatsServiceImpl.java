package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatDto;
import ru.practicum.stats.server.mapper.EndpointHitMapper;
import ru.practicum.stats.server.mapper.ViewStatsMapper;
import ru.practicum.stats.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public void create(EndpointHitDto endpointHitDto) {
        log.info("Добавлена новая запись");
        statsRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris == null) {
            if (unique) {
                return ViewStatsMapper.toListViewStatDto(statsRepository.findAllUniqueIp(start, end));
            } else {
                return ViewStatsMapper.toListViewStatDto(statsRepository.findAllIp(start, end));
            }
        } else {
            if (unique) {
                return ViewStatsMapper.toListViewStatDto(statsRepository.findUniqueIpByUris(start, end, uris));
            } else {
                return ViewStatsMapper.toListViewStatDto(statsRepository.findIpByUris(start, end, uris));
            }
        }
    }
}

