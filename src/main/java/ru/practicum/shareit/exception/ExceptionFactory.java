package ru.practicum.shareit.exception;

import ru.practicum.shareit.exception.exceptions.*;

public class ExceptionFactory {

    public static EntityNotFoundException entityNotFound(String entityType, Long id) {
        return new EntityNotFoundException(entityType + " с ID " + id + " не найден");
    }

    public static AccessDeniedException accessDenied(String message) {
        return new AccessDeniedException(message);
    }

    public static InvalidDataException invalidData(String message) {
        return new InvalidDataException(message);
    }

    public static EntityAlreadyExistsException entityAlreadyExists(String message) {
        return new EntityAlreadyExistsException(message);
    }

}
