package ru.practicum.mainservice.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.mainservice.comment.dto.CommentFullDto;
import ru.practicum.mainservice.comment.dto.CommentShortDto;
import ru.practicum.mainservice.comment.model.Comment;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {

    CommentShortDto commentToCommentShortDto(Comment comment);

    CommentFullDto commentToCommentFullDto(Comment comment);

    Comment commentFullDtoToComment(CommentFullDto commentFullDto);
}
