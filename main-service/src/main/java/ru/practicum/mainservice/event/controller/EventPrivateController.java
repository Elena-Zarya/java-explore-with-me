package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.dto.EventShortDto;
import ru.practicum.mainservice.event.dto.NewEventDto;
import ru.practicum.mainservice.event.dto.UpdateEventUserRequest;
import ru.practicum.mainservice.event.service.EventService;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;

    @GetMapping("/{userId}/events")
    public ResponseEntity<List<EventShortDto>> getUserAllEvents(@PathVariable Long userId,
                                                                @RequestParam(defaultValue = "0") Integer from,
                                                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received GET request: get events user id {}", userId);
        return new ResponseEntity<>(eventService.getUserAllEvents(userId, from, size), HttpStatus.OK);
    }

    @PostMapping("/{userId}/events")
    public ResponseEntity<EventFullDto> addUserEvent(@PathVariable Long userId,
                                                     @Valid @RequestBody(required = false) NewEventDto newEventDto) {
        log.info("Received POST request: add new event user id {}", userId);
        return new ResponseEntity<>(eventService.addUserEvent(userId, newEventDto), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> getUserEvent(@PathVariable Long userId,
                                                     @PathVariable Long eventId) {
        log.info("Received GET request: get user event id {}", eventId);
        return new ResponseEntity<>(eventService.getUserEvent(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> updateUserEvent(@PathVariable Long userId,
                                                        @PathVariable Long eventId,
                                                        @RequestBody(required = false) UpdateEventUserRequest updateEventUserRequest) {
        log.info("Received PATCH request: update event id {}", eventId);
        return new ResponseEntity<>(eventService.updateUserEvent(userId, eventId, updateEventUserRequest), HttpStatus.OK);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getUserEventRequests(@PathVariable Long userId,
                                                                              @PathVariable Long eventId) {
        log.info("Received GET request: get event requests user id {} ", userId);
        return new ResponseEntity<>(eventService.getUserEventRequests(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateUserEventRequests(@PathVariable Long userId,
                                                                                  @PathVariable Long eventId,
                                                                                  @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("Received PATCH request: update event requests user id ={}", userId);
        return new ResponseEntity<>(eventService.updateUserEventRequests(userId, eventId, updateRequest), HttpStatus.OK);
    }
}

