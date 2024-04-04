package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.exception.exceptions.*;


import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        return buildResponseEntity(ex, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({UserNotFoundException.class, EntityNotFoundException.class, BookingNotFoundException.class, ItemNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleNotFoundException(RuntimeException ex) {
        return buildResponseEntity(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex) {
        return buildResponseEntity(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleNotAuthorizedException(NotAuthorizedException ex) {
        return buildResponseEntity(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({IllegalArgumentException.class, InvalidDataException.class, CustomBadRequestException.class, InvalidBookingTimeException.class, ItemUnavailableException.class, UnsupportedStatusException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequestException(RuntimeException ex) {
        return buildResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ItemRequestNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleItemRequestNotFoundException(ItemRequestNotFoundException ex) {
        return buildResponseEntity(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ItemOwnershipException.class)
    public ResponseEntity<Map<String, Object>> handleItemOwnershipException(ItemOwnershipException ex) {
        return buildResponseEntity(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookingOwnershipException.class)
    public ResponseEntity<Map<String, Object>> handleBookingOwnershipException(BookingOwnershipException ex) {
        return buildResponseEntity(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        String message = "Request parameter conversion error";
        if ("state".equals(ex.getName())) {
            message = "Unknown state: " + ex.getValue();
        } else {
            message = ex.getMessage();
        }
        body.put("error", message);
        body.put("message", message);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
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
