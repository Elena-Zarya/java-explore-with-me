package ru.practicum.mainservice.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.mainservice.event.dto.EventShortDto;
import ru.practicum.mainservice.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentShortDto {
    private Long id;
    private UserDto author;
    private EventShortDto event;
    private String text;
    private LocalDateTime createdOn;
}
