package ru.practicum.shareit.exception;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.exceptions.InternalServerException;

import static org.junit.jupiter.api.Assertions.assertThrows; import static org.junit.jupiter.api.Assertions.assertEquals;
public class InternalServerExceptionTest {
    @Test public void testInternalServerExceptionWhenConstructorCalledThenExceptionThrown() { // Arrange String expectedMessage = "An internal server error occurred";
        // Act and Assert
        String expectedMessage = "This is a bad request";

        InternalServerException exception = assertThrows(
                InternalServerException.class,
                () -> { throw new InternalServerException(expectedMessage);
                }
        );

        // Assert that the exception is thrown
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testInternalServerExceptionWhenConstructorCalledThenMessageSet() {
        // Arrange
        String expectedMessage = null;

        // Act
        InternalServerException exception = new InternalServerException(expectedMessage);

        // Assert that the message is set correctly
        assertEquals(expectedMessage, exception.getMessage());
    }
}