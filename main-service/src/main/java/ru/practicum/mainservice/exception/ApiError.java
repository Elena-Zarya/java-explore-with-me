package ru.practicum.mainservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApiError {
    private String status;
    private String reason;
    private String message;
    private String timestamp;
}
