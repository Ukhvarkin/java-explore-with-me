package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.server.model.EndpointHit;
import ru.practicum.stats.server.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT new ru.practicum.stats.server.model.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
        "FROM EndpointHit h " +
        "WHERE h.timestamp BETWEEN ?1 and ?2 " +
        "GROUP BY h.app, h.uri, h.ip " +
        "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStats> findAllUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.stats.server.model.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
        "FROM EndpointHit h " +
        "WHERE h.timestamp BETWEEN ?1 and ?2 and h.uri in ?3 " +
        "GROUP BY h.app, h.uri, h.ip " +
        "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStats> findUniqueIpByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.stats.server.model.ViewStats(h.app, h.uri, COUNT(h.app)) " +
        "FROM EndpointHit h " +
        "WHERE h.timestamp BETWEEN ?1 and ?2 " +
        "GROUP BY h.app, h.uri " +
        "ORDER BY COUNT(h.app) DESC")
    List<ViewStats> findAllIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.stats.server.model.ViewStats(h.app, h.uri, COUNT(h.app)) " +
        "FROM EndpointHit h " +
        "WHERE h.timestamp BETWEEN ?1 and ?2 and h.uri in ?3 " +
        "GROUP BY h.app, h.uri " +
        "ORDER BY COUNT(h.app) DESC")
    List<ViewStats> findIpByUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}