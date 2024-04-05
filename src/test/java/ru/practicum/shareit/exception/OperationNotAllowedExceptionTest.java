package ru.practicum.shareit.exception;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.exceptions.OperationNotAllowedException;

import static org.junit.jupiter.api.Assertions.assertThrows; import static org.junit.jupiter.api.Assertions.assertEquals;
public class OperationNotAllowedExceptionTest {
    @Test public void testConstructorWhenCalledWithMessageThenThrowException() { // Arrange String expectedMessage = "Operation not allowed";
        String expectedMessage = null;
        Exception exception = assertThrows(OperationNotAllowedException.class, () -> {
            throw new OperationNotAllowedException(expectedMessage);
        });

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testGetMessageWhenCalledThenReturnMessage() {
        // Arrange
        String expectedMessage = "Operation not allowed";
        OperationNotAllowedException exception = new OperationNotAllowedException(expectedMessage);

        // Act
        String actualMessage = exception.getMessage();

        // Assert
        assertEquals(expectedMessage, actualMessage);
    }
}