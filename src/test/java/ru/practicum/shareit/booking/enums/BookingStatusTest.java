package ru.practicum.shareit.booking.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookingStatusTest {

    @Test
    public void testEnumValues() {
        // Ensure all expected enum values are present
        BookingStatus[] statuses = BookingStatus.values();
        assertEquals(4, statuses.length);
        assertEquals("WAITING", statuses[0].name());
        assertEquals("APPROVED", statuses[1].name());
        assertEquals("REJECTED", statuses[2].name());
        assertEquals("CANCELED", statuses[3].name());
    }

    @Test
    public void testValueOfValidName() {
        // Test the valueOf method for a valid name
        assertEquals(BookingStatus.WAITING, BookingStatus.valueOf("WAITING"));
        assertEquals(BookingStatus.APPROVED, BookingStatus.valueOf("APPROVED"));
        assertEquals(BookingStatus.REJECTED, BookingStatus.valueOf("REJECTED"));
        assertEquals(BookingStatus.CANCELED, BookingStatus.valueOf("CANCELED"));
    }

    @Test
    public void testValueOfInvalidName() {
        // Test the valueOf method for an invalid name, expecting an exception
        assertThrows(IllegalArgumentException.class, () -> BookingStatus.valueOf("NON_EXISTENT"));
    }

    @Test
    public void testOrdinalValues() {
        // Verify the ordinal values if the order is significant
        assertEquals(0, BookingStatus.WAITING.ordinal());
        assertEquals(1, BookingStatus.APPROVED.ordinal());
        assertEquals(2, BookingStatus.REJECTED.ordinal());
        assertEquals(3, BookingStatus.CANCELED.ordinal());
    }
}
