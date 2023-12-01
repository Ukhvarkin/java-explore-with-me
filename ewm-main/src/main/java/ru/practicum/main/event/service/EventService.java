package ru.practicum.main.event.service;

import ru.practicum.main.enums.ReactionType;
import ru.practicum.main.enums.State;
import ru.practicum.main.event.dto.EventFilterParams;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.reaction.dto.ReactionDto;
import ru.practicum.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> getByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getByPublic(EventFilterParams filterParams,
                                    HttpServletRequest request);

    EventFullDto getEventByIdPublic(Long eventId, HttpServletRequest request);

    List<EventShortDto> getByPrivate(Long userId, int from, int size);

    EventFullDto addByPrivate(Long userId, NewEventDto newEventDto);

    EventFullDto getByIdPrivate(Long userId, Long eventId);

    EventFullDto updateByPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequestsByPrivate(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatusByPrivate(Long userId, Long eventId,
                                                         EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    ReactionDto addReaction(Long eventId, Long userId, ReactionType reaction);

    void deleteReaction(Long eventId, Long userId, ReactionType reactionType);
}
