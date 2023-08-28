package ru.practicum.mainservice.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.comment.dto.CommentFullDto;
import ru.practicum.mainservice.comment.dto.CommentShortDto;
import ru.practicum.mainservice.comment.dto.NewCommentDto;
import ru.practicum.mainservice.comment.dto.UpdateUserCommentDto;
import ru.practicum.mainservice.comment.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "comments/user/{userId}")
@Slf4j
@RequiredArgsConstructor
public class CommentPrivateController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentShortDto>> getAllCommentsByUser(@PathVariable Long userId,
                                                                      @RequestParam(defaultValue = "0") Integer from,
                                                                      @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received GET request: get all comments user id {}", userId);
        return new ResponseEntity<>(commentService.getAllCommentsByUser(userId, from, size), HttpStatus.OK);
    }

    @PostMapping("/{eventId}")
    public ResponseEntity<CommentFullDto> addUserNewComment(@PathVariable Long userId, @PathVariable Long eventId,
                                                            @Valid @RequestBody(required = false) NewCommentDto newCommentDto) {
        log.info("Received POST request: add new comment user id {}", userId);
        return new ResponseEntity<>(commentService.addUserNewComment(userId, eventId, newCommentDto), HttpStatus.CREATED);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentFullDto> getCommentByUser(@PathVariable Long userId,
                                                           @PathVariable Long commentId) {
        log.info("Received GET request: get user comment id {}", commentId);
        return new ResponseEntity<>(commentService.getCommentByUser(userId, commentId), HttpStatus.OK);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentFullDto> updateCommentByUser(@PathVariable Long userId,
                                                              @PathVariable Long commentId,
                                                              @Valid @RequestBody(required = false) UpdateUserCommentDto updateUserCommentDto) {
        log.info("Received PATCH request: update comment id {}", commentId);
        return new ResponseEntity<>(commentService.updateCommentByUser(userId, commentId, updateUserCommentDto), HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentFullDto> deleteCommentByUser(@PathVariable Long userId,
                                                              @PathVariable Long commentId,
                                                              @Valid @RequestBody(required = false) UpdateUserCommentDto updateUserCommentDto) {
        log.info("Received DELETE request: delete comment id {}", commentId);
        commentService.deleteCommentByUser(userId, commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
