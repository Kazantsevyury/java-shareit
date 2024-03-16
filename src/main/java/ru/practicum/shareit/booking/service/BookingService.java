package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import java.util.Collection;

public interface BookingService {
    BookingDto addBooking(BookingDto bookingDto);

    BookingDto updateBooking(BookingDto bookingDto);

    Collection<BookingDto> getAllBookings();

    BookingDto getBookingById(long bookingId);

    void removeBooking(long bookingId);
}
