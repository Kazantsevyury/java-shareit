package ru.practicum.shareit.booking.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;

import ru.practicum.shareit.booking.dto.GetBookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.exceptions.*;
import ru.practicum.shareit.item.model.Item;


import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    private final BookingMapper bookingMapper;

    @Override
    public Booking findBooking(final Long bookingId) {
        return bookingStorage.findBookingById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с id '" + bookingId + "' не найдено."));
    }

    private void checkItemAvailability(final Item item) {
        if (!item.getAvailable()) {
            throw new ItemUnavailableException("Вещь недоступна для бронирования.");
        }
    }

    public Iterable<Booking> getAllSortedBookingsFromUser(final GetBookingState state, Iterable<Booking> result,
                                                          final Long userId) {
        switch (state) {
            case ALL:
                result = bookingStorage.findAllByItemOwnerId(userId);
                break;
            case CURRENT:
                result = bookingStorage.findCurrentBookingsByOwnerId(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                result = bookingStorage.findPastBookingsByOwnerId(userId, LocalDateTime.now());
                break;
            case FUTURE:
                result = bookingStorage.findFutureBookingsByOwnerId(userId, LocalDateTime.now());
                break;
            case WAITING:
                result = bookingStorage.findBookingsByOwnerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                result = bookingStorage.findBookingsByOwnerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
        }
        return result;
    }

    public Iterable<Booking> getAllSortedBookingsFromBooker(final GetBookingState state, Iterable<Booking> result,
                                                            final Long bookerId) {
        switch (state) {
            case ALL:
                result = bookingStorage.findAllByBookerId(bookerId);
                break;
            case CURRENT:
                result = bookingStorage.findCurrentBookingsByBookerId(bookerId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                result = bookingStorage.findPastBookingsByBookerId(bookerId, LocalDateTime.now());
                break;
            case FUTURE:
                result = bookingStorage.findFutureBookingsByBookerId(bookerId, LocalDateTime.now());
                break;
            case WAITING:
                result = bookingStorage.findBookingsByBookerIdAndStatus(bookerId, BookingStatus.WAITING);
                break;
            case REJECTED:
                result = bookingStorage.findBookingsByBookerIdAndStatus(bookerId, BookingStatus.REJECTED);
                break;
        }
        return result;
    }

    @Override
    public Booking pureSave(Booking booking) {
        return bookingStorage.save(booking);
    }
}
