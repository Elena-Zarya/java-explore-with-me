package ru.practicum.mainservice.event.service;

import ru.practicum.mainservice.event.dto.*;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> getUserAllEvents(long userId, int from, int size);

    EventFullDto addUserEvent(long userId, NewEventDto newEventDto);

    EventFullDto getUserEvent(long userId, long eventId);

    EventFullDto updateUserEvent(long userId, long eventId, UpdateEventUserRequest updateRequest);

    List<EventFullDto> getAdminAllEvents(List<Long> users,
                                         List<String> states,
                                         List<Long> categories,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         int from,
                                         int size);

    EventFullDto updateAdminEvent(long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getAllEvents(String text,
                                     List<Long> categories,
                                     Boolean paid,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     Boolean onlyAvailable,
                                     String sort,
                                     Integer from,
                                     Integer size, HttpServletRequest request);

    EventFullDto getEventById(long id, HttpServletRequest request);

    EventFullDto getEventFullById(long id);

    List<ParticipationRequestDto> getUserEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateUserEventRequests(Long userId, Long eventId,
                                                           EventRequestStatusUpdateRequest updateRequest);
}
