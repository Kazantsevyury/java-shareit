package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingState;

import java.util.Collection;
import java.util.List;


public interface BookingService {
    BookingResponseDto addBooking(Long userId, AddBookingDto bookingDto);

    BookingResponseDto acknowledgeBooking(Long userId, Long bookingId, Boolean approved);

    BookingResponseDto getBookingById(Long userId, Long bookingId);

    List<BookingResponseDto> getAllBookingsFromUser(Long userId, BookingState state);

    List<BookingResponseDto> getAllOwnerBookings(Long userId, BookingState state);
}