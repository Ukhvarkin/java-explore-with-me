package ru.practicum.main.reaction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.enums.ReactionType;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.reaction.dto.ReactionDto;
import ru.practicum.main.reaction.mapper.ReactionMapper;
import ru.practicum.main.reaction.model.Reaction;
import ru.practicum.main.reaction.repository.ReactionRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ReactionRepository reactionRepository;

    @Override
    @Transactional
    public ReactionDto addReaction(Long eventId, Long userId, ReactionType reactionType) {
        log.info("Добавление реакции: {}, на событие с id: {}, пользователем с id: {}", reactionType, eventId, userId);
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        Optional<Reaction> existingReaction = reactionRepository.findByUserIdAndEventId(userId, eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор не может оценивать свое событие");
        }

        if (existingReaction.isPresent()) {
            log.warn("Пользователь: {} уже оценил событие: {}", userId, eventId);
            return ReactionMapper.toDto(existingReaction.get());
        }
        log.info("Реакция добавлена");
        updateEventRating(eventId);
        return ReactionMapper.toDto(reactionRepository.save(ReactionMapper.toModel(event, user, reactionType)));
    }

    @Override
    @Transactional
    public void deleteReaction(Long eventId, Long userId, ReactionType reaction) {
        log.info("Удаление реакции: {}, события с id: {}, пользователем с id: {}", reaction, eventId, userId);
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        Optional<Reaction> existingReaction = reactionRepository.findByUserIdAndEventId(userId, eventId);

        if (existingReaction.isPresent()) {
            reactionRepository.delete(existingReaction.get());
            updateEventRating(eventId);
        } else {
            throw new ConflictException(
                "Пользователь: " + user.getName() + "не оценивал событие: " + event.getTitle());
        }
    }

    private void updateEventRating(Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        long likes = reactionRepository.countByEventIdAndReactionType(eventId, ReactionType.LIKE);
        long dislikes = reactionRepository.countByEventIdAndReactionType(eventId, ReactionType.DISLIKE);

        double rating;
        if (likes + dislikes > 0) {
            rating = (double) likes / (likes + dislikes);
        } else {
            rating = 0.0;
        }

        event.setRatingValue(rating);
        eventRepository.updateEventRating(eventId, rating);
        log.info("Обновление рейтинга события с id: {}", eventId);
    }

    @Override
    public List<ReactionDto> getReactions(Long userId, Long eventId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        if (eventId != null) {
            List<Reaction> result =
                reactionRepository.findByUserIdAndEventId(userId, eventId, pageable);
            return result.stream()
                .map(ReactionMapper::toDto)
                .collect(Collectors.toList());
        }
        return reactionRepository.findByUserId(userId, pageable).stream()
            .map(ReactionMapper::toDto)
            .collect(Collectors.toList());
    }
}
