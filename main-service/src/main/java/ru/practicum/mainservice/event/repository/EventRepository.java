package ru.practicum.mainservice.event.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "SELECT e.* " +
            "FROM events e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (UPPER(e.annotation) LIKE UPPER(?1) OR ?1 Is Null) " +
            "AND (e.category_id IN (?2) OR ?2 Is Null) " +
            "AND (e.paid = ?3 OR ?3 Is Null) " +
            "AND (e.event_date > ?4 OR (e.event_date > ?6 AND ?4 is null)) " +
            "AND (e.event_date < ?5 OR ?5 Is Null) ", nativeQuery = true)
    List<Event> getAllEventsByParams(String text,
                                     List<Long> categories,
                                     Boolean paid,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     LocalDateTime now,
                                     Pageable page);

    @Query(value = "SELECT e.* " +
            "FROM events e " +
            "WHERE (e.initiator_id IN(?1) OR ?1 Is Null) " +
            "AND (e.state IN (?2) OR ?2 Is Null) " +
            "AND (e.category_id IN (?3) OR ?3 Is Null) " +
            "AND (e.event_date > ?4 OR ?4 Is Null) " +
            "AND (e.event_date < ?5 OR ?5 Is Null) ", nativeQuery = true)
    List<Event> getAdminEventsByParams(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest page);

    Optional<Event> findByIdAndInitiatorId(long eventId, long userId);

    List<Event> findAllByCategoryId(long categoryId);
}
