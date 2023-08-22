package ru.practicum.mainservice.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.dto.EventShortDto;
import ru.practicum.mainservice.event.dto.NewEventDto;
import ru.practicum.mainservice.event.model.Event;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {
    Event eventFullDtoToEvent(EventFullDto eventFullDto);

    EventFullDto eventToEventFullDto(Event event);

    EventShortDto eventToEventShortDto(Event event);

    Event newEventDtoToEvent(NewEventDto newEventDto);

    default Category mapIdToCategory(Long catId) {
        return new Category(catId, null);
    }

    default Long mapCategoryToId(Category category) {
        return category.getId();
    }
}
