package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.GetBookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;


public interface BookingService {
    Booking pureSave(Booking booking);

    Booking findBooking(final Long bookingId);

    List<Booking> findAllByItemId(Long itemId);

    List<Booking> findAllByItemIdIn(List<Long> itemIds);

    Booking getBookingByIdAndUserId(Long bookingId, Long userId);

    Page<Booking> findAllByOwnerIdAndStatus(Long ownerId, BookingStatus bookingStatus, Pageable pageable);

    Iterable<Booking> findAllByItemOwnerId(Long userId, Pageable pageable);

    Iterable<Booking> findCurrentBookingsByOwnerId(Long userId, LocalDateTime now, LocalDateTime now1, Pageable pageable);

    Iterable<Booking> findPastBookingsByOwnerId(Long userId, LocalDateTime now, Pageable pageable);

    Iterable<Booking> findFutureBookingsByOwnerId(Long userId, LocalDateTime now, Pageable pageable);

    Iterable<Booking> findBookingsByOwnerIdAndStatus(Long userId, BookingStatus bookingStatus, Pageable pageable);

    Iterable<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    Iterable<Booking> findCurrentBookingsByBookerId(Long bookerId, LocalDateTime now, LocalDateTime now1, Pageable pageable);

    Iterable<Booking> findPastBookingsByBookerId(Long bookerId, LocalDateTime now, Pageable pageable);

    Iterable<Booking> findFutureBookingsByBookerId(Long bookerId, LocalDateTime now, Pageable pageable);

    Iterable<Booking> findBookingsByBookerIdAndStatus(Long bookerId, BookingStatus bookingStatus, Pageable pageable);
}
