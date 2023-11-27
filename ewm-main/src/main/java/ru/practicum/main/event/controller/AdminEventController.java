package ru.practicum.main.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.enums.State;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.service.EventService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> get(@RequestParam(value = "users", required = false) List<Long> users,
                           @RequestParam(value = "states", required = false) List<State> states,
                           @RequestParam(value = "categories", required = false) List<Long> categories,
                           @RequestParam(value = "rangeStart", required = false)
                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                           @RequestParam(value = "rangeEnd", required = false)
                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                           @RequestParam(value = "from", defaultValue = "0") int from,
                           @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info(
            "GET : Запрос списка мероприятий с параметрами - " +
                "users: {}, states: {}, categories: {}, rangeStart: {}, rangeEnd: {}, from: {}, size: {}",
            users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long eventId,
                        @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("GET : Запрос на модификацию мероприятия с id: {}", eventId);
        return eventService.updateByAdmin(eventId, updateEventAdminRequest);
    }
}
