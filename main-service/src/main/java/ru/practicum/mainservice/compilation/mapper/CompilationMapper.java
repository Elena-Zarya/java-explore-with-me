package ru.practicum.mainservice.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.mainservice.compilation.dto.CompilationDto;
import ru.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.mainservice.compilation.model.Compilation;
import ru.practicum.mainservice.event.model.Event;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {

//    NewCompilationDto compilationToNewCompilationDto(Compilation compilation);

    CompilationDto compilationToCompilationDto(Compilation compilation);

    Compilation compilationDtoToCompilation(CompilationDto compilationDto);

    Compilation newCompilationDtoToCompilation(NewCompilationDto newCompilationDto);

    default List<Event> mapIdToEvent(List<Long> eventsId) {
        List<Event> events = new ArrayList<>();
        if (eventsId != null) {
            for (Long eventId : eventsId) {
                Event event = new Event();
                        event.setId(eventId);
                events.add(event);
            }
        }
        return events;
    }

//    default List<Long> mapEventsId(List<Event> events) {
//        List<Long> eventsId = new ArrayList<>();
//        for (Event event : events) {
//            eventsId.add(event.getId());
//        }
//        return eventsId;
//    }
}
