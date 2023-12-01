package ru.practicum.main.reaction.mapper;

import lombok.RequiredArgsConstructor;
import ru.practicum.main.enums.ReactionType;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.reaction.dto.ReactionDto;
import ru.practicum.main.reaction.model.Reaction;
import ru.practicum.main.user.model.User;

@RequiredArgsConstructor
public class ReactionMapper {
    public static ReactionDto toDto(Reaction reaction) {
        return ReactionDto.builder()
            .userId(reaction.getUser().getId())
            .eventId(reaction.getEvent().getId())
            .reactionType(reaction.getReactionType())
            .build();
    }

    public static Reaction toModel(Event event, User user, ReactionType reactionType) {
        return Reaction.builder()
            .user(user)
            .event(event)
            .reactionType(reactionType)
            .build();
    }
}
