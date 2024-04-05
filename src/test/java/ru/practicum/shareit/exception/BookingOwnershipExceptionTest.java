package ru.practicum.shareit.exception;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.exceptions.BookingOwnershipException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookingOwnershipExceptionTest {

    @Test
    public void testExceptionMessage() {
        String expectedMessage = "User does not own the booking";
        BookingOwnershipException exception = assertThrows(BookingOwnershipException.class, () -> {
            throw new BookingOwnershipException(expectedMessage);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }
}
