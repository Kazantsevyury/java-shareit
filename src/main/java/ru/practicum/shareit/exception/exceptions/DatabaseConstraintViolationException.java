package ru.practicum.shareit.exception.exceptions;

public class DatabaseConstraintViolationException extends RuntimeException {
    public DatabaseConstraintViolationException(String message) {
        super(message);
    }
}