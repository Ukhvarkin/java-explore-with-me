package ru.practicum.main.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.main.enums.State;
import ru.practicum.main.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e " +
        "WHERE e.initiator.id IN :users " +
        "AND e.state IN :states " +
        "AND e.category.id IN :categories " +
        "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd")
    List<Event> findByParams(@Param("users") List<Long> users,
                             @Param("states") List<State> states,
                             @Param("categories") List<Long> categories,
                             @Param("rangeStart") LocalDateTime rangeStart,
                             @Param("rangeEnd") LocalDateTime rangeEnd, Pageable pageable);

    @Query("SELECT e FROM Event e " +
        "WHERE e.state = 'PUBLISHED' " +
        "AND (:text IS NULL OR " +
        "(LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
        "LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')))) " +
        "AND (:categories IS NULL OR e.category.id IN :categories) " +
        "AND (:paid IS NULL OR e.paid = :paid) " +
        "AND ((" +
        "cast(:rangeStart as timestamp) IS NULL AND " +
        "cast(:rangeEnd as timestamp) IS NULL) OR " +
        "(e.eventDate > CURRENT_TIMESTAMP" +
        ")) " +
        "AND (:onlyAvailable = false OR (e.confirmedRequests < e.participantLimit)) " +
        "ORDER BY " +
        "CASE WHEN :sort = 'EVENT_DATE' THEN e.eventDate END ASC, " +
        "CASE WHEN :sort = 'VIEWS' THEN e.views END ASC, " +
        "CASE WHEN :sort = 'RATING' THEN e.ratingValue END DESC"
    )
    List<Event> findAllEventsPublic(@Param("text") String text,
                                    @Param("categories") List<Long> categories,
                                    @Param("paid") Boolean paid,
                                    @Param("rangeStart") LocalDateTime rangeStart,
                                    @Param("rangeEnd") LocalDateTime rangeEnd,
                                    @Param("onlyAvailable") Boolean onlyAvailable,
                                    @Param("sort") String sort, Pageable pageable);

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    long countByCategoryId(long catId);

    List<Event> findAllByIdIn(List<Long> events);

    @Modifying
    @Query("UPDATE Event e SET e.ratingValue = :rating WHERE e.id = :eventId")
    void updateEventRating(@Param("eventId") Long eventId, @Param("rating") Double rating);
}
