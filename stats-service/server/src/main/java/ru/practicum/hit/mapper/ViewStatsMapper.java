package ru.practicum.hit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.hit.model.ViewStats;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ViewStatsMapper {
    ViewStatsDto viewStatsToDto(ViewStats viewStats);

    ViewStats dtoToViewStats(ViewStatsDto viewStatsDto);
}
