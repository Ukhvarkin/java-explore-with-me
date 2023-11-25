package ru.practicum.main.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {
    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getByUserId(@PathVariable(name = "userId") long userId) {
        log.info("GET : Запрос на список заявок пользователя с id: {}", userId);
        return requestService.get(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto add(@PathVariable(name = "userId") long userId,
                                       @RequestParam(value = "eventId") long eventId) {
        log.info("POST : Запрос на участие в событии с id: {}, пользователя с id: {}", eventId, userId);
        return requestService.add(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable(name = "userId") long userId,
                                          @PathVariable(name = "requestId") long requestId) {
        log.info("PATCH : Отмена запроса с id: {}, на участие пользователя с id: {}", requestId, userId);
        return requestService.update(userId, requestId);
    }
}
