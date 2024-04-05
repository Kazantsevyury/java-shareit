package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.exceptions.DatabaseConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DatabaseConstraintViolationExceptionTest {

    @Test
    public void testExceptionMessage() {
        // Arrange
        String expectedMessage = "Constraint violation occurred";

        // Act and Assert
        DatabaseConstraintViolationException exception = assertThrows(DatabaseConstraintViolationException.class, () -> {
            throw new DatabaseConstraintViolationException(expectedMessage);
        });

        // Verify that the message is as expected
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testExceptionWithNullMessage() {
        // Arrange
        String expectedMessage = null;

        // Act and Assert
        DatabaseConstraintViolationException exception = assertThrows(DatabaseConstraintViolationException.class, () -> {
            throw new DatabaseConstraintViolationException(expectedMessage);
        });

        // Verify that the message is null
        assertEquals(expectedMessage, exception.getMessage());
    }
}
