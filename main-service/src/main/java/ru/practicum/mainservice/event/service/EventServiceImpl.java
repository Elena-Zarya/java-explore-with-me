package ru.practicum.mainservice.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mainservice.Pages;
import ru.practicum.mainservice.category.mapper.CategoryMapper;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.service.CategoryService;
import ru.practicum.mainservice.event.dto.*;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.mapper.LocationMapper;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.model.Location;
import ru.practicum.mainservice.State;
import ru.practicum.mainservice.event.model.StateAction;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.event.repository.LocationRepository;
import ru.practicum.mainservice.exception.ConditionsNotMetException;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.IncorrectRequestException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;
import ru.practicum.mainservice.request.mapper.RequestMapper;
import ru.practicum.mainservice.request.model.Request;
import ru.practicum.mainservice.request.model.Status;
import ru.practicum.mainservice.request.repository.RequestRepository;
import ru.practicum.mainservice.user.dto.UserDto;
import ru.practicum.mainservice.user.mapper.UserMapper;
import ru.practicum.mainservice.user.service.UserService;
import ru.practicum.statsclient.StatsClient;
import ru.practicum.mainservice.event.model.QEvent;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final CategoryService categoryService;
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventMapper eventMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final LocationMapper locationMapper;
    private final RequestMapper requestMapper;
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getUserAllEvents(long userId, int from, int size) {
        Sort sortByCreated = Sort.by(Sort.Direction.ASC, "id");
        PageRequest page = Pages.getPage(from, size, sortByCreated);
        List<Event> events = eventRepository.findAll(page).toList();
        log.info("Get events list by user {}", userId);

        List<EventShortDto> eventFullDtoList = events.stream()
                .map(eventMapper::eventToEventShortDto)
                .collect(Collectors.toList());
        for (EventShortDto eventShortDtos : eventFullDtoList) {
            eventShortDtos.setConfirmedRequests(getConfirmedRequests(eventShortDtos.getId()));
        }
        return eventFullDtoList;
    }

    @Transactional
    @Override
    public EventFullDto addUserEvent(long userId, NewEventDto newEventDto) {
        checkEventDate(newEventDto.getEventDate(), 2);
        Event event = eventMapper.newEventDtoToEvent(newEventDto);
        event.setCategory(categoryMapper.dtoToCategory(categoryService.getCategoryById(newEventDto.getCategory())));
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(userMapper.userDtoToUser(userService.getUserById(userId)));
        event.setState(State.PENDING);
        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        if (event.getPaid() == null) {
            event.setPaid(false);
        }
        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }

        try {
            Location location = locationRepository.save(event.getLocation());
            event.setLocation(location);
            event = eventRepository.save(event);
        } catch (Exception e) {
            throw new ConflictException(e.getMessage());
        }
        log.info("Add event: {}", event.getTitle());
        return eventMapper.eventToEventFullDto(event);
    }

    @Override
    public EventFullDto getUserEvent(long userId, long eventId) {
        UserDto initiator = userService.getUserById(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=13 was not found"));
        log.info("Get event: {}", event.getId());
        return eventMapper.eventToEventFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto updateUserEvent(long userId, long eventId, UpdateEventUserRequest updateRequest) {
        UserDto initiator = userService.getUserById(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event not found with id = %s and userId = %s", eventId, userId)));

        checkEventDate(updateRequest.getEventDate(), 2);

        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Event must not be published");
        }
        if (updateRequest.getAnnotation() != null) {
            if (updateRequest.getAnnotation().length() < 20 || updateRequest.getAnnotation().length() > 2000) {
                throw new IncorrectRequestException("Incorrect annotation");
            }
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getCategory() != null) {
            Category category = categoryMapper.dtoToCategory(categoryService.getCategoryById(updateRequest.getCategory()));
            event.setCategory(category);
        }
        if (updateRequest.getDescription() != null) {
            if (updateRequest.getDescription().length() < 20 ||
                    updateRequest.getDescription().length() > 7000) {
                throw new IncorrectRequestException("Incorrect description");
            }
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            event.setEventDate(updateRequest.getEventDate());
        }
        if (updateRequest.getLocation() != null) {
            event.setLocation(locationMapper.dtoToLocation(updateRequest.getLocation()));
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
        if (updateRequest.getTitle() != null) {
            if (updateRequest.getTitle().length() < 3 || updateRequest.getTitle().length() > 120) {
                throw new IncorrectRequestException("Incorrect title");
            }
            event.setTitle(updateRequest.getTitle());
        }
        try {
            eventRepository.save(event);
        } catch (Exception e) {
            throw new ConflictException(e.getMessage());
        }
        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction()) {
                case "CANCEL_REVIEW":
                    event.setState(State.CANCELED);
                    break;
                case "SEND_TO_REVIEW":
                    event.setState(State.PENDING);
                    break;
            }
        }

        eventRepository.save(event);
        log.info("Update event: {}", event.getTitle());
        return eventMapper.eventToEventFullDto(event);
    }

    @Override
    public List<EventFullDto> getAdminAllEvents(List<Long> users,
                                                List<String> states,
                                                List<Long> categories,
                                                LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd,
                                                int from,
                                                int size) {

        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        PageRequest page = Pages.getPage(from, size, sort);

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().minusYears(1000);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(1000);
        }

        List<State> stateList = new ArrayList<>();
        if (states != null) {
            for (String state : states) {
                if (state.toUpperCase(Locale.ROOT).equals("PUBLISHED")) {
                    stateList.add(State.PUBLISHED);
                }
                if (state.toUpperCase(Locale.ROOT).equals("PENDING")) {
                    stateList.add(State.PENDING);
                }
                if (state.toUpperCase(Locale.ROOT).equals("CANCELED")) {
                    stateList.add(State.CANCELED);
                }
            }
        }
        BooleanExpression byUsers;
        BooleanExpression byCategory;
        BooleanExpression byState;
        BooleanExpression byEventDateStart = QEvent.event.eventDate.after(rangeStart);
        BooleanExpression byEventDateEnd = QEvent.event.eventDate.before(rangeEnd);

        if (users == null) {
            byUsers = QEvent.event.initiator.id.eq(QEvent.event.initiator.id);
        } else {
            byUsers = QEvent.event.initiator.id.in(users);
        }
        if (categories == null) {
            byCategory = QEvent.event.category.id.eq(QEvent.event.category.id);
        } else {
            byCategory = QEvent.event.category.id.in(categories);
        }
        if (stateList.isEmpty()) {
            byState = QEvent.event.state.eq(QEvent.event.state);
        } else {
            byState = QEvent.event.state.in(stateList);
        }

        List<Event> events = eventRepository.findAll(byUsers
                .and(byCategory)
                .and(byState)
                .and(byEventDateStart)
                .and(byEventDateEnd), page).toList();

        List<EventFullDto> eventFullDtoList = events.stream()
                .map(eventMapper::eventToEventFullDto)
                .collect(Collectors.toList());
        for (EventFullDto event : eventFullDtoList) {
            event.setConfirmedRequests(getConfirmedRequests(event.getId()));
            Map<Long, Long> viewsMap = getViews(event.getId());
            if (!viewsMap.isEmpty()) {
                event.setViews(Math.toIntExact(viewsMap.get(event.getId())));
            }
        }

        log.info("Get events list for admin, size = {}", events.size());

        return eventFullDtoList;
    }

    @Transactional
    @Override
    public EventFullDto updateAdminEvent(long eventId, UpdateEventAdminRequest updateEventAdminRequest) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id = " + eventId));
        if (updateEventAdminRequest.getEventDate() != null) {
            checkEventDate(updateEventAdminRequest.getEventDate(), 1);
        }

        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConditionsNotMetException("Only pending or canceled events can be changed");
        } else {
            if (updateEventAdminRequest.getStateAction() != null && updateEventAdminRequest.getStateAction()
                    .equals(StateAction.PUBLISH_EVENT.toString())) {
                if (event.getState().equals(State.CANCELED)) {
                    throw new ConditionsNotMetException("Only pending events can be published");
                }
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
            if (updateEventAdminRequest.getStateAction() != null && updateEventAdminRequest.getStateAction()
                    .equals(StateAction.REJECT_EVENT.toString())) {
                if (event.getPublishedOn() != null && event.getPublishedOn().isBefore(LocalDateTime.now())) {
                    throw new ConditionsNotMetException("The event cannot be rejected because it has already been published.");
                }
                event.setState(State.CANCELED);
            }
        }
        if (updateEventAdminRequest.getAnnotation() != null) {
            if (updateEventAdminRequest.getAnnotation().length() < 20 || updateEventAdminRequest.getAnnotation().length() > 2000) {
                throw new IncorrectRequestException("Incorrect annotation");
            }
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoryMapper
                    .dtoToCategory(categoryService.getCategoryById(updateEventAdminRequest.getCategory()));
            event.setCategory(category);
        }
        if (updateEventAdminRequest.getDescription() != null) {
            if (updateEventAdminRequest.getDescription().length() < 20 ||
                    updateEventAdminRequest.getDescription().length() > 7000) {
                throw new IncorrectRequestException("Incorrect description");
            }
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(locationMapper.dtoToLocation(updateEventAdminRequest.getLocation()));
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            if (updateEventAdminRequest.getTitle().length() < 3 || updateEventAdminRequest.getTitle().length() > 120) {
                throw new IncorrectRequestException("Incorrect title");
            }
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        try {
            Event updateEvent = eventRepository.save(event);
            log.info("Update event: {}", event.getTitle());
            return eventMapper.eventToEventFullDto(updateEvent);
        } catch (Exception e) {
            throw new ConflictException(e.getMessage());
        }
    }

    @Override
    public List<EventShortDto> getAllEvents(String text,
                                            List<Long> categories,
                                            Boolean paid,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            Boolean onlyAvailable,
                                            String sort,
                                            Integer from,
                                            Integer size, HttpServletRequest request) {

        Sort sortS = Sort.by(Sort.Direction.ASC, "eventDate");
        if (sort != null && sort.equals("VIEWS")) {
            sortS = Sort.by(Sort.Direction.ASC, "views");
        }
        if (categories != null) {
            for (Long catId : categories) {
                if (catId < 1) {
                    throw new IncorrectRequestException("Caregories should is not null");
                }
            }
        }

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(1000);
        }
        if (text == null) {
            text = "";
        }

        PageRequest page = Pages.getPage(from, size, sortS);

        BooleanExpression byState = QEvent.event.state.eq(State.PUBLISHED);
        BooleanExpression byAnnotationAnyText = QEvent.event.annotation.likeIgnoreCase("%" + text + "%");
        BooleanExpression byDescriptionAnyText = QEvent.event.description.likeIgnoreCase("%" + text + "%");
        BooleanExpression byEventDateStart = QEvent.event.eventDate.after(rangeStart);
        BooleanExpression byEventDateEnd = QEvent.event.eventDate.before(rangeEnd);
        BooleanExpression byPaid;

        List<Event> events;

        if (paid != null) {
            byPaid = QEvent.event.paid.eq(paid);
            events = eventRepository.findAll(byState
                    .and(byPaid)
                    .and(byEventDateStart)
                    .and(byEventDateEnd)
                    .and((byAnnotationAnyText).or(byDescriptionAnyText)), page).toList();
        } else {
            events = eventRepository.findAll(byState
                    .and(byEventDateStart)
                    .and(byEventDateEnd)
                    .and((byAnnotationAnyText).or(byDescriptionAnyText)), page).toList();
        }


        addNewEndpointHit(request);
        log.info("Выдан список событий ({} шт) по запросу с фильтрами.", events.size());

        return events.stream()
                .map(eventMapper::eventToEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found with id " + id));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException(String.format("Event with id=%d is not published", id));
        }
        log.info("Get event: {}", event.getId());

        EventFullDto eventFullDto = eventMapper.eventToEventFullDto(event);
        eventFullDto.setConfirmedRequests(getConfirmedRequests(event.getId()));

        Map<Long, Long> viewsMap = getViews(event.getId());
        if (!viewsMap.isEmpty()) {
            eventFullDto.setViews(Math.toIntExact(viewsMap.get(event.getId())));
        }
        addNewEndpointHit(request);
        log.info("Get event: {}", event.getId());
        return eventFullDto;
    }

    @Override
    public EventFullDto getEventFullById(long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found with id " + id));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException(String.format("Event with id=%d is not published", id));
        }
        EventFullDto eventFullDto = eventMapper.eventToEventFullDto(event);
        eventFullDto.setConfirmedRequests(getConfirmedRequests(event.getId()));
        log.info("Get event: {}", event.getId());
        return eventFullDto;
    }

    @Override
    public List<ParticipationRequestDto> getUserEventRequests(Long userId, Long eventId) {
        UserDto initiator = userService.getUserById(userId);
        EventFullDto event = getEventFullById(eventId);
        if (event.getInitiator().getId() != initiator.getId()) {
            throw new NotFoundException("Event by user id = " + userId + " not found");
        }
        List<ParticipationRequestDto> requests = requestRepository.findAllByEventIdAndInitiatorId(eventId, userId)
                .stream().map(requestMapper::requestToParticipationRequestDto)
                .collect(Collectors.toList());
        log.info("Get requests user id {} by event id {}", userId, eventId);
        return requests;
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateUserEventRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        UserDto initiator = userService.getUserById(userId);
        EventFullDto eventFullDto = getEventFullById(eventId);

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        if (eventFullDto.getParticipantLimit() > 0 && eventFullDto.getConfirmedRequests() == eventFullDto.getParticipantLimit()) {
            log.info("Event id={} has reached participant limit", eventId);
            throw new ConflictException("Event id= " + eventId + " has reached participant limit");
        }

        List<Request> requests = requestRepository.findAllByIdIn(updateRequest.getRequestIds());
        if (requests != null) {
            List<ParticipationRequestDto> requestsList = requests.stream()
                    .map(requestMapper::requestToParticipationRequestDto)
                    .collect(Collectors.toList());

            for (ParticipationRequestDto requestDto : requestsList) {
                if (!requestDto.getStatus().equals(Status.PENDING)) {
                    throw new ConflictException("Request must have status PENDING");
                }
                if (eventFullDto.getConfirmedRequests() == eventFullDto.getParticipantLimit()) {
                    requestDto.setStatus(Status.REJECTED);
                    requestsList.add(requestDto);
                } else {
                    if (updateRequest.getStatus().equals(Status.CONFIRMED)) {
                        requestDto.setStatus(Status.CONFIRMED);
                        confirmedRequests.add(requestDto);
                    }
                    if (updateRequest.getStatus().equals(Status.REJECTED)) {
                        requestDto.setStatus(Status.REJECTED);
                        rejectedRequests.add(requestDto);
                    }
                }
                Request request = requestMapper.dtoToRequest(requestDto);
                request.setEvent(eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=13 was not found")));
                requestRepository.save(request);
            }
        }
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    private void checkEventDate(LocalDateTime eventDate, int hour) {
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plusHours(hour))) {
            throw new IncorrectRequestException("EventDate should be in the future");
        }
    }

    private void addNewEndpointHit(HttpServletRequest request) {

        EndpointHitDto endpointHit = new EndpointHitDto("ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        ResponseEntity<Object> response = statsClient.addNewEndpointHit(endpointHit);
        log.info("Добавлена статистика {}", response);
    }

    private int getConfirmedRequests(Long eventId) {
        List<Request> requests = requestRepository.findRequestByEventIdAndStatus(eventId, Status.CONFIRMED);
        return requests.size();
    }

    private Map<Long, Long> getViews(long eventId) {
        List<String> uris = List.of("/events/" + eventId);
        String[] urisArray = uris.toArray(new String[uris.size()]);
        String startDate = LocalDateTime.now().minusYears(1000).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endDate = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<ViewStatsDto> response = statsClient.getStats(startDate, endDate, urisArray, true).getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        Collection<ViewStatsDto> viewStatsDtos = objectMapper.convertValue(response,
                new TypeReference<>() {
                });

        Map<Long, Long> viewsMap = new HashMap<>();

        if (viewStatsDtos.size() > 0) {

            for (ViewStatsDto viewStatsDto : viewStatsDtos) {
                String str = viewStatsDto.getUri();
                int index = str.lastIndexOf("/") + 1;
                Long id = Long.parseLong(str.substring(index));
                viewsMap.put(id, viewStatsDto.getHits());
            }
        }
        return viewsMap;
    }
}
