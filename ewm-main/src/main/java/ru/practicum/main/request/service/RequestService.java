package ru.practicum.main.request.service;

import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> get(Long userId);

    ParticipationRequestDto add(Long userId, Long eventId);

    ParticipationRequestDto update(Long userId, Long requestId);
}
