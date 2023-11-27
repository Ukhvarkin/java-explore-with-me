package ru.practicum.main.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.event.dto.EventFilterParams;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> get(@ModelAttribute EventFilterParams filterParams,
                                   HttpServletRequest request) {
        return eventService.getByPublic(filterParams, request);
    }

    @GetMapping("/{eventId}")
    public EventFullDto get(@PathVariable Long eventId,
                            HttpServletRequest request) {
        return eventService.getEventByIdPublic(eventId, request);
    }
}
