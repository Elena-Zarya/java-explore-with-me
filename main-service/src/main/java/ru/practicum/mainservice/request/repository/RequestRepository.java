package ru.practicum.mainservice.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.request.model.Request;
import ru.practicum.mainservice.request.model.Status;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findAllByIdIn(List<Long> requestsId);

    List<Request> findRequestByEventIdAndStatus(Long eventId, Status confirmed);

    @Query(value = "select r.* " +
            "from requests as r " +
            "left join events as e on r.event_id = e.id " +
            "left join users as u on e.initiator_id = u.id " +
            "where r.event_id = ?1 and e.initiator_id = ?2 " +
            "group by r.id ", nativeQuery = true)
    List<Request> findAllByEventIdAndInitiatorId(Long eventId, Long userId);
}
