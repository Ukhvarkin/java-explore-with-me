package ru.practicum.stats.server.mapper;

import ru.practicum.stats.dto.ViewStatDto;
import ru.practicum.stats.server.model.ViewStats;

import java.util.List;
import java.util.stream.Collectors;

public class ViewStatsMapper {

    public static List<ViewStatDto> toListViewStatDto(List<ViewStats> list) {
        return list.stream()
            .map(ViewStatsMapper::toViewStatDto)
            .collect(Collectors.toList());
    }

    public static ViewStatDto toViewStatDto(ViewStats viewStats) {
        return ViewStatDto.builder()
            .app(viewStats.getApp())
            .uri(viewStats.getUri())
            .hits(viewStats.getHits())
            .build();
    }
}
