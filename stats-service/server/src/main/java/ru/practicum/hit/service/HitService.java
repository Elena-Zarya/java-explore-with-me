package ru.practicum.hit.service;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {
    EndpointHitDto addNewEndpointHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
