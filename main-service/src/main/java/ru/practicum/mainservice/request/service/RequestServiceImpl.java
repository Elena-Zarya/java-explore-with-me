package ru.practicum.mainservice.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.shared.State;
import ru.practicum.mainservice.event.service.EventService;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;
import ru.practicum.mainservice.request.mapper.RequestMapper;
import ru.practicum.mainservice.request.model.Request;
import ru.practicum.mainservice.request.model.Status;
import ru.practicum.mainservice.request.repository.RequestRepository;
import ru.practicum.mainservice.user.dto.UserDto;
import ru.practicum.mainservice.user.mapper.UserMapper;
import ru.practicum.mainservice.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventService eventService;
    private final RequestMapper requestMapper;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        UserDto requester = userService.getUserById(userId);
        List<Request> userRequests = requestRepository.findAllByRequesterId(userId);
        if (userRequests.isEmpty()) {
            return new ArrayList<>();
        }
        log.info("Get requests user id {}", userId);
        return userRequests.stream()
                .map(requestMapper::requestToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto addUserRequest(Long userId, Long eventId) {
        EventFullDto event = eventService.getEventFullById(eventId);
        UserDto requester = userService.getUserById(userId);

        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() == event.getParticipantLimit()) {
            log.info("Event id={} has reached participant limit", eventId);
            throw new ConflictException("Event id= " + eventId + " has reached participant limit");
        }

        List<Request> userEventRequests = requestRepository.findAllByRequesterIdAndEventId(userId, eventId);
        if (!userEventRequests.isEmpty()) {
            log.info("Request with requesterId={} and eventId={} already exist", userId, eventId);
            throw new ConflictException("Request with requesterId " + userId + " and eventId " + eventId + " already exist");
        }
        if (event.getInitiator().getId() == userId) {
            log.info("User with id={} must not be equal to initiator", userId);
            throw new ConflictException("User with id=" + userId + " must not be equal to initiator");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            log.info("Event must be published {}", event);
            throw new ConflictException("Event must be published");
        }
        Request newRequest = new Request();
        newRequest.setEvent(eventMapper.eventFullDtoToEvent(event));
        newRequest.setRequester(userMapper.userDtoToUser(requester));
        newRequest.setCreated(LocalDateTime.now());
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            newRequest.setStatus(Status.CONFIRMED);
        } else {
            newRequest.setStatus(Status.PENDING);
        }
        Request savedRequest = requestRepository.save(newRequest);
        log.info("Saved new request id = {}", savedRequest.getId());
        return requestMapper.requestToParticipationRequestDto(savedRequest);
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {
        UserDto requester = userService.getUserById(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found with id " + requestId));
        request.setStatus(Status.CANCELED);
        Request cancelledRequest = requestRepository.save(request);
        log.info("Cancel request id = {}", requestId);
        return requestMapper.requestToParticipationRequestDto(cancelledRequest);
    }
}
