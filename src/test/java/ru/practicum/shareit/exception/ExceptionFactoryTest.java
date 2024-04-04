package ru.practicum.shareit.exception;

import ru.practicum.shareit.exception.exceptions.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionFactoryTest {

    @Test
    void testEntityNotFound() {
        EntityNotFoundException exception = ExceptionFactory.entityNotFound("Тестовый объект", 1L);
        assertEquals("Тестовый объект с ID 1 не найден", exception.getMessage());
    }

    @Test
    void testAccessDenied() {
        AccessDeniedException exception = ExceptionFactory.accessDenied("Доступ запрещен");
        assertEquals("Доступ запрещен", exception.getMessage());
    }

    @Test
    void testInvalidData() {
        InvalidDataException exception = ExceptionFactory.invalidData("Неверные данные");
        assertEquals("Неверные данные", exception.getMessage());
    }

    @Test
    void testEntityAlreadyExists() {
        EntityAlreadyExistsException exception = ExceptionFactory.entityAlreadyExists("Объект уже существует");
        assertEquals("Объект уже существует", exception.getMessage());
    }

    @Test
    void testUserNotFoundException() {
        UserNotFoundException exception = ExceptionFactory.userNotFoundException("Пользователь не найден");
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void testInvalidBookingTime() {
        InvalidBookingTimeException exception = ExceptionFactory.invalidBookingTime("Некорректное время бронирования");
        assertEquals("Некорректное время бронирования", exception.getMessage());
    }

    @Test
    void testNotAuthorizedException() {
        NotAuthorizedException exception = ExceptionFactory.notAuthorizedException("Не авторизован");
        assertEquals("Не авторизован", exception.getMessage());
    }

    @Test
    void testItemUnavailableException() {
        ItemUnavailableException exception = ExceptionFactory.itemUnavailableException("Предмет недоступен");
        assertEquals("Предмет недоступен", exception.getMessage());
    }
}
