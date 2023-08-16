package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.dto.EventShortDto;
import ru.practicum.mainservice.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@Slf4j
@RequiredArgsConstructor
public class EventPublicController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAllEvents(@RequestParam(required = false) String text,
                                                            @RequestParam(required = false) List<Long> categories,
                                                            @RequestParam(required = false) Boolean paid,
                                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                            LocalDateTime rangeStart,
                                                            @RequestParam(required = false)
                                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                            LocalDateTime rangeEnd,
                                                            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                            @RequestParam(required = false) String sort,
                                                            @RequestParam(defaultValue = "0") Integer from,
                                                            @RequestParam(defaultValue = "10") Integer size,
                                                            HttpServletRequest request) {
        log.info("Received GET request: get all  events");
        return new ResponseEntity<>(eventService.getAllEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size, request), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEventById(@PathVariable Long id, HttpServletRequest request) {
        log.info("Received GET request: get event by id {}", id);
        return new ResponseEntity<>(eventService.getEventById(id, request), HttpStatus.OK);
    }
}

