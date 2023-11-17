package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.server.model.StatsHit;
import ru.practicum.stats.server.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<StatsHit, Long> {
    @Query("SELECT new ru.practicum.stats.server.model.ViewStats(sh.app, sh.uri, COUNT(DISTINCT sh.ip)) " +
        "FROM StatsHit sh " +
        "WHERE sh.timestamp BETWEEN :start AND :end " +
        "AND (sh.uri IN :uris OR :uris = NULL) " +
        "GROUP BY sh.app, sh.uri " +
        "ORDER BY COUNT(DISTINCT sh.ip) DESC ")
    List<ViewStats> findAllUniqueIp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                    @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.stats.server.model.ViewStats(sh.app, sh.uri, COUNT(sh.app)) " +
        "FROM StatsHit sh " +
        "WHERE sh.timestamp BETWEEN :start AND :end " +
        "AND (sh.uri IN :uris OR :uris = NULL) " +
        "GROUP BY sh.app, sh.uri " +
        "ORDER BY COUNT(sh.ip) DESC ")
    List<ViewStats> findAllIp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                              @Param("uris") List<String> uris);
}