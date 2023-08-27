package ru.practicum.mainservice.comment.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.Pages;
import ru.practicum.mainservice.State;
import ru.practicum.mainservice.comment.dto.*;
import ru.practicum.mainservice.comment.mapper.CommentMapper;
import ru.practicum.mainservice.comment.model.Comment;
import ru.practicum.mainservice.comment.model.QComment;
import ru.practicum.mainservice.comment.repository.CommentRepository;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.model.QEvent;
import ru.practicum.mainservice.event.service.EventService;
import ru.practicum.mainservice.exception.IncorrectRequestException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.user.dto.UserDto;
import ru.practicum.mainservice.user.mapper.UserMapper;
import ru.practicum.mainservice.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final EventService eventService;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private static final int COUNT_YEARS = 1000;

    @Override
    public List<CommentShortDto> getAllCommentsByEvent(Long eventId, Integer from, Integer size) {
        EventFullDto eventShortDto = eventService.getEventFullById(eventId);

        Sort sortByCreated = Sort.by(Sort.Direction.ASC, "createdOn");
        PageRequest page = Pages.getPage(from, size, sortByCreated);

        List<Comment> comments = commentRepository.findAllByState(State.PUBLISHED, page);
        log.info("Get comments list by event {}", eventId);

        return comments.stream()
                .map(commentMapper::commentToCommentShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentShortDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new NotFoundException("Comment with id = " + id + " not found"));
        log.info("Get comment: {}", id);
        return commentMapper.commentToCommentShortDto(comment);
    }

    @Override
    public List<CommentShortDto> getAllCommentsByUser(Long userId, Integer from, Integer size) {
        UserDto userDto = userService.getUserById(userId);

        Sort sortByCreated = Sort.by(Sort.Direction.ASC, "createdOn");
        PageRequest page = Pages.getPage(from, size, sortByCreated);

        List<Comment> comments = commentRepository.findAllByAuthorId(userId, page);
        log.info("Get comments list by user {}", userId);

        return comments.stream()
                .map(commentMapper::commentToCommentShortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentFullDto addUserNewComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        UserDto userDto = userService.getUserById(userId);
        EventFullDto eventShortDto = eventService.getEventFullById(eventId);

        Comment comment = new Comment();
        comment.setAuthor(userMapper.userDtoToUser(userDto));
        comment.setEvent(eventMapper.eventShortDtoToEvent(eventShortDto));
        comment.setText(newCommentDto.getText());
        comment.setCreatedOn(LocalDateTime.now());
        comment.setState(State.PENDING);

        Comment commentSaved = commentRepository.save(comment);
        log.info("Add comment id = {}", commentSaved.getId());
        return commentMapper.commentToCommentFullDto(commentSaved);
    }

    @Override
    public CommentFullDto getCommentByUser(Long userId, Long commentId) {
        UserDto userDto = userService.getUserById(userId);

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment with id = " + commentId + " not found"));
        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new IncorrectRequestException("Comment with id = " + commentId + " and author id = " + userId + " not found");
        }
        log.info("Get comment id {}", commentId);
        return commentMapper.commentToCommentFullDto(comment);
    }

    @Transactional
    @Override
    public CommentFullDto updateCommentByUser(Long userId, Long commentId, UpdateUserCommentDto updateUserCommentDto) {
        CommentFullDto commentFullDto = getCommentByUser(userId, commentId);
        Comment comment = commentMapper.commentFullDtoToComment(commentFullDto);

        comment.setText(updateUserCommentDto.getText());
        comment.setState(State.PENDING);

        Comment commentUpdate = commentRepository.save(comment);
        log.info("Update comment by author, commentId = {}", commentId);
        return commentMapper.commentToCommentFullDto(commentUpdate);
    }

    @Override
    public List<CommentFullDto> getAdminAllComments(Long userId, State state, Long eventId, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createdOn");
        PageRequest page = Pages.getPage(from, size, sort);

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().minusYears(COUNT_YEARS);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(COUNT_YEARS);
        }

        BooleanExpression byUser;
        BooleanExpression byEvent;
        BooleanExpression byState;
        BooleanExpression byCommentDateStart = QEvent.event.eventDate.after(rangeStart);
        BooleanExpression byCommentDateEnd = QEvent.event.eventDate.before(rangeEnd);

        if (userId == null) {
            byUser = QComment.comment.author.id.eq(QComment.comment.author.id);
        } else {
            byUser = QComment.comment.author.id.eq(userId);
        }
        if (eventId == null) {
            byEvent = QComment.comment.event.id.eq(QComment.comment.event.id);
        } else {
            byEvent = QComment.comment.event.id.eq(eventId);
        }
        if (state == null) {
            byState = QComment.comment.state.eq(QComment.comment.state);
        } else {
            byState = QComment.comment.state.eq(state);
        }

        List<Comment> commentList = commentRepository.findAll(byUser
                .and(byEvent)
                .and(byState)
                .and(byCommentDateStart)
                .and(byCommentDateEnd), page).toList();

        log.info("Get comments list for admin, size = {}", commentList.size());
        return commentList.stream()
                .map(commentMapper::commentToCommentFullDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentFullDto updateAdminComment(Long commentId, UpdateAdminCommentDto updateAdminCommentDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment with id = " + commentId + " not found"));
        if (updateAdminCommentDto.getText() != null) {
            comment.setText(updateAdminCommentDto.getText());
        }
        if (updateAdminCommentDto.getState() != null) {
            comment.setState(updateAdminCommentDto.getState());
        }

        Comment commentUpdate = commentRepository.save(comment);
        log.info("Update comment by admin, commentId = {}", commentId);
        return commentMapper.commentToCommentFullDto(commentUpdate);
    }

    @Transactional
    @Override
    public void deleteCommentByUser(Long userId, Long commentId) {
        CommentFullDto commentFullDto = getCommentByUser(userId, commentId);
        commentRepository.deleteById(commentId);
        log.info("Deleted comment id = {}", commentId);
    }
}
