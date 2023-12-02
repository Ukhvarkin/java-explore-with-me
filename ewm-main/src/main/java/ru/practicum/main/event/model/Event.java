package ru.practicum.main.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.enums.State;
import ru.practicum.main.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "annotation", nullable = false)
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private Category category;

    @Column(name = "confirmed_requests")
    private int confirmedRequests;

    @Column(name = "created")
    private LocalDateTime createdOn;

    @Column(name = "description")
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", referencedColumnName = "id", nullable = false)
    private User initiator;

    @AttributeOverrides(value = {
        @AttributeOverride(name = "lat", column = @Column(name = "lat")),
        @AttributeOverride(name = "lon", column = @Column(name = "lon"))
    })
    private Location location;

    @Column(name = "paid", nullable = false)
    private Boolean paid;

    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit;

    @Column(name = "published")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    private State state;

    @Column(name = "title")
    private String title;

    @Column(name = "views", columnDefinition = "int default 0")
    private Long views;

    @Column(name = "rating", columnDefinition = "float default 0")
    private Double ratingValue;
}
