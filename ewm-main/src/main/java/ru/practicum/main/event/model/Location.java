package ru.practicum.main.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Embeddable;

@Data
@Embeddable
@AllArgsConstructor
@RequiredArgsConstructor
public class Location {
    private Double lat;
    private Double lon;
}
