package ru.practicum.main.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.enums.AdminStateAction;
import ru.practicum.main.enums.RequestStatus;
import ru.practicum.main.enums.State;
import ru.practicum.main.enums.UserStateAction;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.BadRequest;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.mapper.RequestMapper;
import ru.practicum.main.request.model.ParticipationRequest;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.user.repository.UserRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.ViewStatDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient = new StatsClient("http://stats-server:9090", new RestTemplateBuilder());

    @Override
    public List<EventFullDto> getByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        if (users != null || states != null || categories != null || rangeStart != null || rangeEnd != null) {
            List<Event> result =
                eventRepository.findByParams(users, states, categories, rangeStart, rangeEnd, pageable);
            log.info("[admin] : Получение списка событий, размер списка: {}", result.size());

            return result.stream()
                .map(this::getStats)
                .map(EventMapper::toFullDto)
                .collect(Collectors.toList());
        } else {
            return eventRepository.findAll(pageable).stream()
                .map(this::getStats)
                .map(EventMapper::toFullDto)
                .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {
        log.info("[admin] : Модификация события");
        Event existingEvent = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (existingEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException(
                String.format(
                    "Дата начала изменяемого события должна быть не ранее чем за час от даты публикации. EventDate: %s",
                    existingEvent.getEventDate()));
        }
        if (updateEvent.getStateAction() != null) {

            if (existingEvent.getState().equals(State.PUBLISHED)) {
                throw new ConflictException("Cannot publish the event because it's not in the right state: PUBLISHED");
            } else if (existingEvent.getState().equals(State.CANCELED)) {
                throw new ConflictException("Cannot publish the event because it's not in the right state: PUBLISHED");
            } else {
                if (updateEvent.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
                    existingEvent.setState(State.PUBLISHED);
                    log.info("Состояние обновлено: {}", existingEvent.getState());
                }
                if (updateEvent.getStateAction().equals(AdminStateAction.REJECT_EVENT)) {
                    existingEvent.setState(State.CANCELED);
                    log.info("Состояние обновлено: {}", existingEvent.getState());
                }
            }
        }
        if (updateEvent.getAnnotation() != null) {
            existingEvent.setAnnotation(updateEvent.getAnnotation());
            log.info("Аннотация обновлена: {}", existingEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            existingEvent.setCategory(categoryRepository.findById(updateEvent.getCategory())
                .orElseThrow(() -> new NotFoundException(
                    String.format("Category with id=%d was not found", updateEvent.getCategory()))));
        }
        if (updateEvent.getDescription() != null) {
            existingEvent.setDescription(updateEvent.getDescription());
            log.info("Описание обновлено: {}", existingEvent.getDescription());
        }
        if (updateEvent.getEventDate() != null) {
            if (updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ConflictException(
                    String.format("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: %s",
                        updateEvent.getEventDate()));
            }
            existingEvent.setEventDate(updateEvent.getEventDate());
            log.info("Дата обновлена: {}", existingEvent.getEventDate());
        }

        if (updateEvent.getLocation() != null) {
            existingEvent.setLocation(updateEvent.getLocation());
            log.info("Локация обновлена: {}", existingEvent.getLocation());
        }
        if (updateEvent.getPaid() != null) {
            existingEvent.setPaid(updateEvent.getPaid());
            log.info("Флаг о платности мероприятия обновлен: {}", existingEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            existingEvent.setParticipantLimit(updateEvent.getParticipantLimit());
            log.info("Лимит пользователей обновлен: {}", existingEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            existingEvent.setRequestModeration(updateEvent.getRequestModeration());
            log.info("Нужна ли пре-модерация заявок на участие: {}", existingEvent.getRequestModeration());
        }
        if (updateEvent.getTitle() != null) {
            existingEvent.setTitle(updateEvent.getTitle());
            log.info("Заголовок обновлен: {}", existingEvent.getTitle());
        }
        log.info("Событие отредактировано");
        return EventMapper.toFullDto(eventRepository.save(existingEvent));
    }

    @Override
    public List<EventShortDto> getByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, int from,
                                           int size,
                                           HttpServletRequest request) {
        log.info("[public] : Получение списка событий с фильтрацией");
        if (rangeEnd != null && rangeStart != null && rangeEnd.isBefore(rangeStart)) {
            throw new BadRequest("End date is before start date");
        }
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events =
            eventRepository.findAllEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                pageRequest);
        statsClient.create(request);
        return events.stream()
            .map(EventMapper::toShortDto)
            .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByIdPublic(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        }

        statsClient.create(request);
        Event eventWithStats = getStats(event);
        EventFullDto eventFullDto = EventMapper.toFullDto(eventRepository.save(eventWithStats));
        log.info("[public] : Получение события: {}", event.getTitle());
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getByPrivate(Long userId, int from, int size) {
        userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));

        List<EventShortDto> result = eventRepository.findAllByInitiatorId(userId, pageable).stream()
            .map(EventMapper::toShortDto)
            .collect(Collectors.toList());
        log.info("[private] : Получение списка событий пользователя с id = {}", userId);
        return result;
    }

    @Override
    @Transactional
    public EventFullDto addByPrivate(Long userId, NewEventDto newEventDto) {
        log.info("[private] : Добавление нового события: {}", newEventDto);
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException(String.format(
                "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: %s",
                newEventDto.getEventDate()));
        }
        Event event = EventMapper.toModel(newEventDto);

        event.setCategory(categoryRepository.findById(newEventDto.getCategory())
            .orElseThrow(() -> new NotFoundException(
                String.format("Category with id=%d was not found", newEventDto.getCategory()))));
        event.setInitiator(userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId))));
        event.setPublishedOn(LocalDateTime.now());
        event.setViews(0L);
        if (newEventDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        log.info("[private] : Событие: {}, добавлено", event.getTitle());
        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getByIdPrivate(Long userId, Long eventId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        Event eventWithStats = getStats(event);
        EventFullDto eventFullDto = EventMapper.toFullDto(eventRepository.save(eventWithStats));
        log.info("[private] : Получение события: {}", event.getTitle());
        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto updateByPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {
        log.info("[private] : Модификация события");
        Event existingEvent = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (existingEvent.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (updateEvent.getAnnotation() != null) {
            existingEvent.setAnnotation(updateEvent.getAnnotation());
            log.info("Аннотация обновлена: {}", existingEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            existingEvent.setCategory(categoryRepository.findById(updateEvent.getCategory())
                .orElseThrow(() -> new NotFoundException(
                    String.format("Category with id=%d was not found", updateEvent.getCategory()))));
        }

        if (updateEvent.getDescription() != null) {
            existingEvent.setDescription(updateEvent.getDescription());
            log.info("Описание обновлено: {}", existingEvent.getDescription());
        }
        if (updateEvent.getLocation() != null) {
            existingEvent.setLocation(updateEvent.getLocation());
            log.info("Локация обновлена: {}", existingEvent.getLocation());
        }
        if (updateEvent.getPaid() != null) {
            existingEvent.setPaid(updateEvent.getPaid());
            log.info("Флаг о платности мероприятия обновлен: {}", existingEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            existingEvent.setParticipantLimit(updateEvent.getParticipantLimit());
            log.info("Лимит пользователей обновлен: {}", existingEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            existingEvent.setRequestModeration(updateEvent.getRequestModeration());
            log.info("Нужна ли пре-модерация заявок на участие: {}", existingEvent.getRequestModeration());
        }
        if (updateEvent.getTitle() != null) {
            existingEvent.setTitle(updateEvent.getTitle());
            log.info("Заголовок обновлен: {}", existingEvent.getTitle());
        }

        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction().equals(UserStateAction.CANCEL_REVIEW)) {
                existingEvent.setState(State.CANCELED);
                log.info("Статус: {}", existingEvent.getState());
            } else {
                existingEvent.setState(State.PENDING);
                log.info("Статус: {}", existingEvent.getState());
            }
        }
        EventFullDto eventFullDto = EventMapper.toFullDto(eventRepository.save(existingEvent));
        log.info("Пользователь с id: {}, модифицировал событие: {}", userId, eventFullDto);
        return eventFullDto;
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByPrivate(Long userId, Long eventId) {
        List<ParticipationRequestDto> requests =
            requestRepository.findAllByEvent_Initiator_IdAndEvent_Id(userId, eventId)
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
        log.info("Пользователь с id: {}, получил список на участие в событиях: {}", eventId, requests);
        return requests;
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateStatusByPrivate(Long userId, Long eventId,
                                                                EventRequestStatusUpdateRequest updateRequest) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Пользователь " + userId + " не является инициатором события " + eventId);
        }

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ConflictException("The number of participants is limited or pre-moderation is disabled");
        }

        int availableSlots = event.getParticipantLimit() - event.getConfirmedRequests();
        if (updateRequest.getStatus().equals(RequestStatus.CONFIRMED) && availableSlots == 0) {
            throw new ConflictException("Попытка принять заявку на участие в событии, когда лимит уже достигнут");
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        List<ParticipationRequest> participationRequests = updateRequest.getRequestIds()
            .stream()
            .map(id -> requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ParticipationRequest with id=" + id + " was not found")))
            .collect(Collectors.toList());

        participationRequests.forEach(request -> {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConflictException("Request must have status PENDING");
            }

            request.setStatus(updateRequest.getStatus());
            if (updateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                eventRepository.save(event);
            }

            requestRepository.save(request);

            if (updateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
                result.setConfirmedRequests(List.of(RequestMapper.toDto(request)));
            } else {
                result.setRejectedRequests(List.of(RequestMapper.toDto(request)));
            }
        });

        log.info("Статусы обновлены: {}", result);
        return result;
    }

    private Event getStats(Event event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String url = "/events/" + event.getId();

        List<ViewStatDto> viewStats = statsClient
            .get(event.getCreatedOn().format(formatter), LocalDateTime.now().format(formatter), new String[] {url},
                true)
            .getBody();

        viewStats.stream()
            .findFirst()
            .ifPresent(viewStat -> event.setViews(viewStat.getHits()));

        return event;
    }
}
