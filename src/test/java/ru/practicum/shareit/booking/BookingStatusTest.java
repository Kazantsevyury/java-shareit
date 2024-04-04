package ru.practicum.shareit.booking;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.practicum.shareit.booking.enums.BookingStatus;

class BookingStatusTest {

    @Test
    void bookingStatus_ShouldContainExpectedValues() {
        assertEquals(4, BookingStatus.values().length);

        assertNotNull(BookingStatus.valueOf("WAITING"));
        assertNotNull(BookingStatus.valueOf("APPROVED"));
        assertNotNull(BookingStatus.valueOf("REJECTED"));
        assertNotNull(BookingStatus.valueOf("CANCELED"));
    }
}
