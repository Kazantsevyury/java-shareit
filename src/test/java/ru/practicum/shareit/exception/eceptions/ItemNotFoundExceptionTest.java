package ru.practicum.shareit.exception.eceptions;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.exceptions.ItemNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemNotFoundExceptionTest {

    @Test
    public void testItemNotFoundExceptionWithMessage() {
        // Arrange
        String expectedMessage = "Item not found";
        // Act
        ItemNotFoundException exception = new ItemNotFoundException(expectedMessage);
        // Assert
        assertEquals(expectedMessage, exception.getMessage(), "The message should match the one provided at construction time.");
    }

    @Test
    public void testItemNotFoundExceptionWithoutMessage() {
        // Arrange
        String expectedMessage = null;
        // Act
        ItemNotFoundException exception = new ItemNotFoundException(expectedMessage);
        // Assert
        assertEquals(expectedMessage, exception.getMessage(), "The message should be null as no message was provided.");
    }
}
