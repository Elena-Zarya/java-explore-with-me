package ru.practicum.hit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.hit.service.HitService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HitController {
    private final HitService hitService;

    @PostMapping(value = "/hit")
    public ResponseEntity<EndpointHitDto> addNewEndpointHit(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("Received POST request: new EndpointHit");
        return new ResponseEntity<>(hitService.addNewEndpointHit(endpointHitDto), HttpStatus.CREATED);
    }

    @GetMapping(value = "/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                                       @RequestParam(required = false) List<String> uris,
                                                       @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Received GET request: get statistics");
        return new ResponseEntity<>(hitService.getStats(start, end, uris, unique), HttpStatus.OK);
    }
}
