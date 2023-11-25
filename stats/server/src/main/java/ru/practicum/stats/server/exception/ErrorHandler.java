package ru.practicum.stats.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, BadRequest.class})
    public ApiError handleBadRequestException(RuntimeException e) {
        String reason = "Incorrectly made request.";
        return new ApiError(HttpStatus.BAD_REQUEST, reason, e.getMessage(), LocalDateTime.now());
    }
}
