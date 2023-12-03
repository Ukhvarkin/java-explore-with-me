package ru.practicum.main.reaction.service;

import ru.practicum.main.enums.ReactionType;
import ru.practicum.main.reaction.dto.ReactionDto;

import java.util.List;

public interface ReactionService {

    ReactionDto addReaction(Long eventId, Long userId, ReactionType reaction);

    void deleteReaction(Long eventId, Long userId, ReactionType reactionType);

    List<ReactionDto> getReactions(Long userId, Long eventId, int from, int size);
}
