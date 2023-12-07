package ru.practicum.main.reaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.enums.ReactionType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionDto {
    private long userId;
    private long eventId;
    private ReactionType reactionType;
}
