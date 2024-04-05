package ru.practicum.shareit.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.exceptions.UnsupportedStatusException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UnsupportedStatusException Test")
class UnsupportedStatusExceptionTest {

    @Test
    @DisplayName("Test 'UnsupportedStatusException' constructor with a message")
    void testUnsupportedStatusExceptionConstructorWithMessage() {
        String expectedMessage = "Unsupported status provided";
        Exception exception = assertThrows(UnsupportedStatusException.class, () -> {
            throw new UnsupportedStatusException(expectedMessage);
        });
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test 'UnsupportedStatusException' constructor message")
    void testUnsupportedStatusExceptionConstructorMessage() {
        String expectedMessage = "Unsupported status provided";
        UnsupportedStatusException exception = new UnsupportedStatusException(expectedMessage);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
