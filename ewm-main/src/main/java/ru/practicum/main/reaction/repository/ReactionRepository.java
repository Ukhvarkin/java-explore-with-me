package ru.practicum.main.reaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.enums.ReactionType;
import ru.practicum.main.reaction.model.Reaction;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByUserIdAndEventId(Long userId, Long eventId);

    long countByEventIdAndReactionType(Long eventId, ReactionType reactionType);
}
