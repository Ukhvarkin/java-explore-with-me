package ru.practicum.main.event.mapper;

import lombok.RequiredArgsConstructor;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.enums.State;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.Location;
import ru.practicum.main.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EventMapper {

    public static EventFullDto toFullDto(Event event) {

        return EventFullDto.builder()
            .annotation(event.getAnnotation())
            .category(CategoryMapper.toDto(event.getCategory()))
            .confirmedRequests(event.getConfirmedRequests())
            .createdOn(event.getCreatedOn())
            .description(event.getDescription())
            .eventDate(event.getEventDate())
            .id(event.getId())
            .initiator(UserMapper.toShortDto(event.getInitiator()))
            .location(event.getLocation())
            .paid(event.getPaid())
            .participantLimit(event.getParticipantLimit())
            .publishedOn(event.getPublishedOn())
            .requestModeration(event.getRequestModeration())
            .state(event.getState())
            .title(event.getTitle())
            .views(event.getViews())
            .build();
    }

    public static EventShortDto toShortDto(Event event) {

        return EventShortDto.builder()
            .annotation(event.getAnnotation())
            .category(CategoryMapper.toDto(event.getCategory()))
            .confirmedRequests(event.getConfirmedRequests())
            .eventDate(event.getEventDate())
            .id(event.getId())
            .initiator(UserMapper.toShortDto(event.getInitiator()))
            .paid(event.getPaid())
            .title(event.getTitle())
            .views(event.getViews())
            .build();
    }

    public static Event toModel(NewEventDto newEventDto) {
        return Event.builder()
            .annotation(newEventDto.getAnnotation())
            .createdOn(LocalDateTime.now())
            .description(newEventDto.getDescription())
            .eventDate(newEventDto.getEventDate())
            .location(new Location(newEventDto.getLocation().getLat(), newEventDto.getLocation().getLon()))
            .paid(newEventDto.isPaid())
            .participantLimit(newEventDto.getParticipantLimit())
            .requestModeration(newEventDto.getRequestModeration())
            .state(State.PENDING)
            .title(newEventDto.getTitle())
            .build();
    }

    public static List<EventShortDto> toShortListDto(List<Event> events) {
        return events.stream()
            .map(EventMapper::toShortDto)
            .collect(Collectors.toList());
    }
}
