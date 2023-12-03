package ru.practicum.main.reaction.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.enums.ReactionType;
import ru.practicum.main.reaction.dto.ReactionDto;
import ru.practicum.main.reaction.service.ReactionService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/rating")
public class PrivateRatingController {
    private final ReactionService reactionService;

    @PostMapping("/{eventId}/reaction")
    @ResponseStatus(HttpStatus.CREATED)
    public ReactionDto addReaction(@Positive @PathVariable(name = "userId") Long userId,
                                   @Positive @PathVariable(name = "eventId") Long eventId,
                                   @RequestParam(name = "reactionType") ReactionType reactionType
    ) {
        return reactionService.addReaction(eventId, userId, reactionType);
    }

    @DeleteMapping("/{eventId}/reaction")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReaction(@Positive @PathVariable(name = "userId") Long userId,
                               @Positive @PathVariable(name = "eventId") Long eventId,
                               @RequestParam(name = "reactionType") ReactionType reactionType
    ) {
        reactionService.deleteReaction(eventId, userId, reactionType);
    }

    @GetMapping
    public List<ReactionDto> getReactions(@Positive @PathVariable(value = "userId") Long userId,
                                          @RequestParam(value = "eventId", required = false) Long eventId,
                                          @RequestParam(value = "from", defaultValue = "0") int from,
                                          @RequestParam(value = "size", defaultValue = "10") int size) {
        return reactionService.getReactions(userId, eventId, from, size);
    }
}
