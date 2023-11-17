package ru.practicum.stats.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatDto;
import ru.practicum.stats.server.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatsController {
    private final StatsService service;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> create(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.info("Сохранение запроса к эндпоинту: {}", endpointHitDto.getUri());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(endpointHitDto));
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatDto>> get(
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
        @RequestParam(required = false) List<String> uris,
        @RequestParam(defaultValue = "false", required = false) boolean unique) {
        log.info("Запрос на получение статистики по посещениям");
        return ResponseEntity.ok(service.get(start, end, uris, unique));
    }
}
