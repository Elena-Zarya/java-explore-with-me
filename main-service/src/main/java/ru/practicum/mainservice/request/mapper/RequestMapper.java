package ru.practicum.mainservice.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;
import ru.practicum.mainservice.request.model.Request;
import ru.practicum.mainservice.user.model.User;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RequestMapper {

    Request dtoToRequest(ParticipationRequestDto participationRequestDto);

    ParticipationRequestDto requestToParticipationRequestDto(Request request);

    default Event mapIdToEvent(Long eventId) {
        Event event = new Event();
        event.setId(eventId);
        return event;
    }

    default Long mapEventToId(Event event) {
        return event.getId();
    }

    default User mapIdToUser(Long userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }

    default Long mapUserToId(User user) {
        return user.getId();
    }
}
