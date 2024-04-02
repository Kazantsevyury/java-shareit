package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;


public interface BookingService {
    Booking pureSave(Booking booking);

    Booking findBooking(final Long bookingId);

    List<Booking> findAllByItemId(Long itemId);

    List<Booking> findAllByItemIdIn(List<Long> itemIds);
    Booking getBookingByIdAndUserId(Long bookingId, Long userId);
}
