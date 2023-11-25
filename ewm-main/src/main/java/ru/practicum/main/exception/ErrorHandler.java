package ru.practicum.main.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundException.class})
    public ApiError handleNotFoundException(RuntimeException e) {
        String reason = "The required object was not found.";
        String message = e.getMessage();
        return new ApiError(HttpStatus.NOT_FOUND, reason, message, LocalDateTime.now());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({ConstraintViolationException.class, ConflictException.class})
    public ApiError handleConflictException(RuntimeException e) {
        String reason = "Integrity constraint has been violated.";
        String message = e.getMessage();
        return new ApiError(HttpStatus.CONFLICT, reason, message, LocalDateTime.now());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({ForbiddenException.class})
    public ApiError handleForbiddenException(ForbiddenException e) {
        String reason = "For the requested operation the conditions are not met.";
        String message = e.getMessage();
        return new ApiError(HttpStatus.FORBIDDEN, reason, message, LocalDateTime.now());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, BadRequest.class})
    public ApiError handleBadRequestException(RuntimeException e) {
        String reason = "Incorrectly made request.";
        String message = "Field: category. Error: must not be blank. Value: null";
        return new ApiError(HttpStatus.BAD_REQUEST, reason, message, LocalDateTime.now());
    }
}
