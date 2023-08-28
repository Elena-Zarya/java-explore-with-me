package ru.practicum.mainservice.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.comment.dto.CommentShortDto;
import ru.practicum.mainservice.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping(path = "/comments")
@Slf4j
@RequiredArgsConstructor
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping("event/{eventId}")
    public ResponseEntity<List<CommentShortDto>> getAllCommentsForEvent(@PathVariable Long eventId,
                                                                        @RequestParam(defaultValue = "0") Integer from,
                                                                        @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received GET request: get all comments for event {}", eventId);
        return new ResponseEntity<>(commentService.getAllCommentsByEvent(eventId, from, size), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentShortDto> getCommentById(@PathVariable Long id) {
        log.info("Received GET request: get comment by id {}", id);
        return new ResponseEntity<>(commentService.getCommentById(id), HttpStatus.OK);
    }
}
