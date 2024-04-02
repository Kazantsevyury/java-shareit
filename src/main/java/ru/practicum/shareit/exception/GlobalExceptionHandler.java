package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.shareit.exception.exceptions.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            AccessDeniedException.class,
            UserNotFoundException.class,
            InvalidDataException.class,
            EntityAlreadyExistsException.class,
            EntityNotFoundException.class,
            InvalidBookingTimeException.class,
            IllegalArgumentException.class,
            ItemNotFoundException.class,
            CustomBadRequestException.class,
            BookingNotFoundException.class,
            ItemUnavailableException.class,
            NotAuthorizedException.class
    })
    public ResponseEntity<Map<String, Object>> handleKnownExceptions(RuntimeException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (ex instanceof AccessDeniedException) status = HttpStatus.FORBIDDEN;
        else if (ex instanceof UserNotFoundException || ex instanceof EntityNotFoundException || ex instanceof BookingNotFoundException || ex instanceof ItemNotFoundException) status = HttpStatus.NOT_FOUND;
        else if (ex instanceof EntityAlreadyExistsException) status = HttpStatus.CONFLICT;
        else if (ex instanceof NotAuthorizedException) status = HttpStatus.UNAUTHORIZED;
        else if (ex instanceof IllegalArgumentException || ex instanceof InvalidDataException || ex instanceof CustomBadRequestException || ex instanceof InvalidBookingTimeException || ex instanceof ItemUnavailableException) status = HttpStatus.BAD_REQUEST;

        return buildResponseEntity(ex, status);
    }

    @ExceptionHandler(ItemRequestNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleItemRequestNotFoundException(ItemRequestNotFoundException ex) {
        return buildResponseEntity(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookingOwnershipException.class)
    public ResponseEntity<Map<String, Object>> handleBookingOwnershipException(BookingOwnershipException ex) {
        return buildResponseEntity(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnsupportedStatusException.class)
    public ResponseEntity<Map<String, Object>> handleUnsupportedStatusException(UnsupportedStatusException ex) {
        return buildResponseEntity(ex, HttpStatus.BAD_REQUEST); // Использование BAD_REQUEST
    }

    private ResponseEntity<Map<String, Object>> buildResponseEntity(RuntimeException ex, HttpStatus status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", ex.getMessage());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, status);
    }

}
