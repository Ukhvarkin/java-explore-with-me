package ru.practicum.main.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.main.enums.RequestStatus;
import ru.practicum.main.enums.State;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.mapper.RequestMapper;
import ru.practicum.main.request.model.ParticipationRequest;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> get(Long userId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        return requestRepository.findAllByRequester_Id(userId).stream()
            .map(RequestMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto add(Long userId, Long eventId) {
        if (!requestRepository.findAllByEventIdAndRequesterId(eventId, userId).isEmpty()) {
            throw new ConflictException(
                String.format("User %d already has participation request to event %d", userId, eventId));
        }
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));
        if (userId == event.getInitiator().getId()) {
            throw new ConflictException(String.format("User %d is an initiator of event %d", userId, eventId));
        }
        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Event must be published.");
        }
        if (event.getParticipantLimit() <=
            requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED) &&
            event.getParticipantLimit() != 0) {
            throw new ConflictException(String.format("Event %d has maximum confirmed requests", eventId));
        }
        ParticipationRequest request = ParticipationRequest.builder()
            .event(event)
            .requester(user)
            .created(LocalDateTime.now())
            .build();
        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }
        if (!event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        log.info("Запрос пользователя с id: {}, на участие в событии c id: {} - создан", userId, eventId);
        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto update(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findById(requestId)
            .orElseThrow(() -> new NotFoundException(String.format("Request with id=%d was not found", requestId)));
        if (request.getRequester().getId() != userId) {
            throw new RuntimeException();
        }
        request.setStatus(RequestStatus.CANCELED);
        log.info("Пользователь с id: {}, отменил запрос на участие", userId);
        return RequestMapper.toDto(requestRepository.save(request));
    }
}
