package ru.practicum.mainservice.request.service;

import ru.practicum.mainservice.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto addUserRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelUserRequest(Long userId, Long requestId);
//    List<ParticipationRequestDto> getUserRequestsByEvent(Long userId, Long eventId);

//    ParticipationRequestDto getUserRequestByEvent(Long userId, Long eventId);

//    List<ParticipationRequestDto> findAllByIdIn(List<Long> requestsId);

//    ParticipationRequestDto updateRequest(ParticipationRequestDto requestDto);
}
