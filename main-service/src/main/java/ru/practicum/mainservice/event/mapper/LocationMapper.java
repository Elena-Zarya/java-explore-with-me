package ru.practicum.mainservice.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.mainservice.event.dto.LocationDto;
import ru.practicum.mainservice.event.model.Location;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LocationMapper {

    Location dtoToLocation(LocationDto locationDto);

    LocationDto locationToDto (Location location);
}
