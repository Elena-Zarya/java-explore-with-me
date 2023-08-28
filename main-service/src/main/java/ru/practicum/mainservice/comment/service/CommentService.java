package ru.practicum.mainservice.comment.service;

import ru.practicum.mainservice.shared.State;
import ru.practicum.mainservice.comment.dto.*;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    List<CommentShortDto> getAllCommentsByEvent(Long eventId, Integer from, Integer size);

    CommentShortDto getCommentById(Long id);

    List<CommentShortDto> getAllCommentsByUser(Long userId, Integer from, Integer size);

    CommentFullDto addUserNewComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentFullDto getCommentByUser(Long userId, Long commentId);

    CommentFullDto updateCommentByUser(Long userId, Long commentId, UpdateUserCommentDto updateUserCommentDto);

    List<CommentFullDto> getAdminAllComments(Long userId, State state, Long eventId, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    CommentFullDto updateAdminComment(Long commentId, UpdateAdminCommentDto updateAdminCommentDto);

    void deleteCommentByUser(Long userId, Long commentId);
}
