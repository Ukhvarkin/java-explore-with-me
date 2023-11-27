package ru.practicum.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ViewStatsRequest {
    private String start;
    private String end;
    private String[] uris;
    private boolean unique;
}
