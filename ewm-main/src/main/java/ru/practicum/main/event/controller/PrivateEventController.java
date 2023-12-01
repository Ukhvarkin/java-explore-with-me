package ru.practicum.main.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.enums.ReactionType;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.event.service.EventService;
import ru.practicum.main.reaction.dto.ReactionDto;
import ru.practicum.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getAll(@PositiveOrZero @PathVariable(name = "userId") Long userId,
                                      @RequestParam(value = "from", defaultValue = "0") int from,
                                      @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("GET : Запрос на получение списка мероприятий для пользователя с id: {}, " +
                "с параметрами - from: {}, size: {}",
            userId, from, size);
        return eventService.getByPrivate(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto add(@Positive @PathVariable(name = "userId") Long userId,
                            @Valid @RequestBody NewEventDto newEventDto) {
        log.info("POST : Запрос на добавление нового мероприятия пользователя с id: {}. " +
            "Параметры: {}", userId, newEventDto);
        return eventService.addByPrivate(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getById(@Positive @PathVariable(name = "userId") Long userId,
                                @Positive @PathVariable(name = "eventId") Long eventId) {
        log.info("GET : Запрос на получение мероприятия с id: {} пользователя с id: {}", eventId, userId);
        return eventService.getByIdPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@Positive @PathVariable(name = "userId") Long userId,
                               @Positive @PathVariable(name = "eventId") Long eventId,
                               @Valid @RequestBody UpdateEventUserRequest updateEvent) {
        log.info("PATCH : Запрос на модификацию события с id: {}, пользователем с id: {}. " +
            "Параметры: {}", eventId, userId, updateEvent);
        return eventService.updateByPrivate(userId, eventId, updateEvent);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@Positive @PathVariable(name = "userId") Long userId,
                                                     @Positive @PathVariable(name = "eventId") Long eventId) {
        log.info("GET : Запрос на получение информации о запросах на участие в событии с id: {}, " +
            "пользователя с id: {}", eventId, userId);
        return eventService.getRequestsByPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequest(@Positive @PathVariable(name = "userId") Long userId,
                                                        @Positive @PathVariable(name = "eventId") Long eventId,
                                                        @Valid @RequestBody
                                                        EventRequestStatusUpdateRequest updateRequest) {
        log.info("PATCH : Запрос на изменение статуса заявки в событии с id: {}, пользователя с id: {}. " +
            "Параметры: {}", eventId, userId, updateRequest);
        return eventService.updateStatusByPrivate(userId, eventId, updateRequest);
    }

    @PostMapping("/{eventId}/reaction")
    @ResponseStatus(HttpStatus.CREATED)
    public ReactionDto addReaction(@Positive @PathVariable(name = "userId") Long userId,
                                @Positive @PathVariable(name = "eventId") Long eventId,
                                @RequestParam(name = "reactionType") ReactionType reactionType
    ) {
        return eventService.addReaction(eventId, userId, reactionType);
    }

    @DeleteMapping("/{eventId}/reaction")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReaction(@Positive @PathVariable(name = "userId") Long userId,
                               @Positive @PathVariable(name = "eventId") Long eventId,
                               @RequestParam(name = "reactionType") ReactionType reactionType
    ) {
        eventService.deleteReaction(eventId, userId, reactionType);
    }
}
