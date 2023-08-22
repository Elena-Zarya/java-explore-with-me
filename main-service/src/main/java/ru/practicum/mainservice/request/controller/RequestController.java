package ru.practicum.mainservice.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;
import ru.practicum.mainservice.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(@PathVariable Long userId) {
        log.info("Received GET request: get requests user id {}", userId);
        return new ResponseEntity<>(requestService.getUserRequests(userId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> addUserRequest(@PathVariable Long userId,
                                                                  @RequestParam Long eventId) {
        log.info("Received POST request: add request user id {} in event id {}", userId, eventId);
        return new ResponseEntity<>(requestService.addUserRequest(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping("{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelUserRequest(@PathVariable Long userId,
                                                                     @PathVariable Long requestId) {
        log.info("Received PATCH request: cancel request user id {} in event id {}", userId, requestId);
        return new ResponseEntity<>(requestService.cancelUserRequest(userId, requestId), HttpStatus.OK);
    }
}
