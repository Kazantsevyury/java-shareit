package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;


public interface BookingService {
    Booking pureSave(Booking booking);

    Booking findBooking(final Long bookingId);

}
