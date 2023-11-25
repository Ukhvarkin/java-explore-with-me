package ru.practicum.main.request.mapper;

import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.model.ParticipationRequest;

public class RequestMapper {
    public static ParticipationRequestDto toDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
            .created(request.getCreated())
            .event(request.getEvent().getId())
            .id(request.getId())
            .requester(request.getRequester().getId())
            .status(request.getStatus())
            .build();
    }
}
