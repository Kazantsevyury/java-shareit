package ru.practicum.shareit.exception.eceptions;


import org.junit.jupiter.api.Test; import org.junit.jupiter.api.Assertions;
import ru.practicum.shareit.exception.exceptions.CustomBadRequestException;

public class CustomBadRequestExceptionTest {

    @Test
    public void testCustomBadRequestExceptionWhenMessageIsNotNullThenThrowException() {
        // Arrange
        String expectedMessage = "This is a bad request";

        // Act and Assert
        Exception exception = Assertions.assertThrows(CustomBadRequestException.class, () -> {
            throw new CustomBadRequestException(expectedMessage);
        });

        // Verify that the message is the one we passed
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }
}