package ru.practicum.mainservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        return new ApiError(HttpStatus.CONFLICT.toString(), "Integrity constraint has been violated. \"%s\"",
                e.getMessage(), LocalDateTime.now().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final DataIntegrityViolationException e) {
        return new ApiError(HttpStatus.CONFLICT.toString(), "Integrity constraint has been violated. \"%s\"",
                e.getMessage(), LocalDateTime.now().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        return new ApiError(HttpStatus.NOT_FOUND.toString(), "The required object was not found. \"%s\"",
                e.getMessage(), LocalDateTime.now().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectRequestException(final IncorrectRequestException e) {
        return new ApiError(HttpStatus.BAD_REQUEST.toString(), "Incorrectly made request. \"%s\".", e.getMessage(),
                LocalDateTime.now().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConditionsNotMetException e) {
        return new ApiError(HttpStatus.CONFLICT.toString(), "For the requested operation the conditions are not met. \"%s\"",
                e.getMessage(), LocalDateTime.now().toString());
    }
}
