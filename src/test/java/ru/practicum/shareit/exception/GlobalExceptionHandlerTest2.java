package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.exception.exceptions.ItemRequestNotFoundException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTest2 {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Инициализация моков
    }

    @Test
    public void testHandleItemRequestNotFoundException() {
        ItemRequestNotFoundException ex = new ItemRequestNotFoundException("Item request not found");
        ResponseEntity<Map<String, Object>> response = handler.handleItemRequestNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Item request not found", response.getBody().get("message"));
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().get("status"));
    }

    @Test
    public void testHandleMethodArgumentTypeMismatch() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException("123", null, "state", null, new IllegalArgumentException("Unknown state: 123"));
        ResponseEntity<Map<String, Object>> response = handler.handleMethodArgumentTypeMismatch(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Unknown state: 123", response.getBody().get("error"));
        assertEquals("Unknown state: 123", response.getBody().get("message"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().get("status"));
    }

    @Test
    public void testHandleMethodArgumentTypeMismatchElseCase() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException("123", null, "state", null, new IllegalArgumentException("Unknown state: 123"));
        ResponseEntity<Map<String, Object>> response = handler.handleMethodArgumentTypeMismatch(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Unknown state: 123", response.getBody().get("error"));
        assertEquals("Unknown state: 123", response.getBody().get("message"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().get("status"));
    }
}
