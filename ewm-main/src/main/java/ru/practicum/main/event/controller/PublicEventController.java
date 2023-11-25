package ru.practicum.main.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    List<EventShortDto> get(@RequestParam(value = "text", required = false) String text,
                            @RequestParam(value = "categories", required = false) List<Long> categories,
                            @RequestParam(value = "paid", required = false) Boolean paid,
                            @RequestParam(value = "rangeStart", required = false)
                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                            @RequestParam(value = "rangeEnd", required = false)
                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                            @RequestParam(value = "onlyAvailable", required = false) Boolean onlyAvailable,
                            @RequestParam(value = "sort", required = false) String sort,
                            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                            HttpServletRequest request) {
        return eventService.getByPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{eventId}")
    public EventFullDto get(@PathVariable Long eventId,
                            HttpServletRequest request) {
        return eventService.getEventByIdPublic(eventId, request);
    }
}
