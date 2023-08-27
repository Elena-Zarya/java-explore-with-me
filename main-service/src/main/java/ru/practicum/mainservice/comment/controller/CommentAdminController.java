package ru.practicum.mainservice.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.comment.dto.CommentFullDto;
import ru.practicum.mainservice.comment.dto.UpdateAdminCommentDto;
import ru.practicum.mainservice.comment.service.CommentService;
import ru.practicum.mainservice.shared.State;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "comments/admin")
@Slf4j
@RequiredArgsConstructor
public class CommentAdminController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentFullDto>> getAdminAllComments(@RequestParam(required = false) Long userId,
                                                                    @RequestParam(required = false) State state,
                                                                    @RequestParam(required = false) Long eventId,
                                                                    @RequestParam(required = false)
                                                                    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                                    LocalDateTime rangeStart,
                                                                    @RequestParam(required = false)
                                                                    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                                    LocalDateTime rangeEnd,
                                                                    @RequestParam(defaultValue = "0") Integer from,
                                                                    @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received GET request: get admin all comments");
        return new ResponseEntity<>(commentService.getAdminAllComments(userId, state, eventId, rangeStart, rangeEnd,
                from, size), HttpStatus.OK);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentFullDto> updateAdminComment(@PathVariable Long commentId,
                                                             @Valid @RequestBody UpdateAdminCommentDto updateAdminCommentDto) {
        log.info("Received PATCH request: update comment id {}", commentId);
        return new ResponseEntity<>(commentService.updateAdminComment(commentId, updateAdminCommentDto), HttpStatus.OK);
    }
}
