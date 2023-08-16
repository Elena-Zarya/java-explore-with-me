package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.dto.UpdateEventAdminRequest;
import ru.practicum.mainservice.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@Slf4j
@RequiredArgsConstructor
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getAdminAllEvents(@RequestParam(required = false) List<Long> users,
                                                                @RequestParam(required = false) List<String> states,
                                                                @RequestParam(required = false) List<Long> categories,
                                                                @RequestParam(required = false)
                                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                                LocalDateTime rangeStart,
                                                                @RequestParam(required = false)
                                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                                LocalDateTime rangeEnd,
                                                                @RequestParam(defaultValue = "0") Integer from,
                                                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received GET request: get admin all  events");
        return new ResponseEntity<>(eventService.getAdminAllEvents(users, states, categories, rangeStart, rangeEnd,
                from, size), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateAdminEvent(@PathVariable Long eventId,
                                                    @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Received PATCH request: update event id {}", eventId);
        return new ResponseEntity<>(eventService.updateAdminEvent(eventId, updateEventAdminRequest), HttpStatus.OK);
    }
}
