package ru.practicum.mainservice.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.mainservice.shared.State;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentFullDto {

    private Long id;
    private UserDto author;
    private EventFullDto event;
    private String text;
    private LocalDateTime createdOn;
    private State state;
}
