package ru.practicum.hit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.hit.mapper.HitMapper;
import ru.practicum.hit.mapper.ViewStatsMapper;
import ru.practicum.hit.model.EndpointHit;
import ru.practicum.hit.model.ViewStats;
import ru.practicum.hit.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HitServiceImpl implements HitService {

    private final HitRepository hitRepository;
    private final HitMapper hitMapper;
    private final ViewStatsMapper viewStatsMapper;

    @Transactional
    @Override
    public EndpointHitDto addNewEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHitSaved = hitRepository.save(hitMapper.dtoToEndpointHit(endpointHitDto));
        return hitMapper.EndpointHitToDto(endpointHitSaved);

    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        PageRequest pageable = PageRequest.of(0, 10);
        if (unique) {
            List<ViewStats> viewStatsList = hitRepository.findUniqueViewStats(start, end, uris, pageable);
            return viewStatsList.stream()
                    .map(viewStatsMapper::viewStatsToDto)
                    .collect(Collectors.toList());
        } else {
            List<ViewStats> viewStatsList = hitRepository.findViewStats(start, end, uris, pageable);
            return viewStatsList.stream()
                    .map(viewStatsMapper::viewStatsToDto)
                    .collect(Collectors.toList());
        }
    }
}
