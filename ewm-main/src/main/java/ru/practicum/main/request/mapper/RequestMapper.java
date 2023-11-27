package ru.practicum.main.request.mapper;

import ru.practicum.main.enums.RequestStatus;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.model.ParticipationRequest;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;

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

    public static ParticipationRequest toModel(Event event, User requester) {
        RequestStatus status;

        if (event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        } else {
            status = event.getRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED;
        }

        return ParticipationRequest.builder()
            .created(LocalDateTime.now())
            .event(event)
            .requester(requester)
            .status(status)
            .build();

    }
}
