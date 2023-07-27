package ru.practicum.hit.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.hit.model.EndpointHit;
import ru.practicum.hit.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<EndpointHit, Long> {
    @Query(" SELECT new ru.practicum.hit.model.ViewStats(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndpointHit e " +
            "WHERE e.timestamp BETWEEN ?1 AND ?2 " +
            "AND (e.uri IN (?3) OR (?3) is NULL) " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(DISTINCT e.ip) DESC ")
    List<ViewStats> findUniqueViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, PageRequest pageable);


    @Query(" SELECT new ru.practicum.hit.model.ViewStats(e.app, e.uri, COUNT(e.ip)) " +
            "FROM EndpointHit e " +
            "WHERE e.timestamp BETWEEN ?1 AND ?2 " +
            "AND (e.uri IN (?3) OR (?3) is NULL) " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC ")
    List<ViewStats> findViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, PageRequest pageable);
}
