package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ExceptionFactory;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto addBooking(BookingDto bookingDto) {
        final Booking booking = bookingMapper.bookingDtoToBooking(bookingDto);
        final Booking addedBooking = bookingStorage.add(booking);
        log.info("Добавление нового бронирования: {}", addedBooking);
        Booking resultBooking = bookingStorage.findById(addedBooking.getId());
        if (resultBooking == null) {
            throw ExceptionFactory.entityNotFound("Бронирование", addedBooking.getId());
        }
        return bookingMapper.bookingToBookingDto(resultBooking);
    }

    @Override
    public BookingDto updateBooking(BookingDto bookingDto) {
        Booking booking = bookingStorage.findById(bookingDto.getId());
        if (booking == null) {
            throw ExceptionFactory.entityNotFound("Бронирование", bookingDto.getId());
        }
        final Booking updatedBooking = bookingMapper.bookingDtoToBooking(bookingDto);
        bookingStorage.update(updatedBooking);
        log.info("Обновление бронирования с id {}: {}", bookingDto.getId(), updatedBooking);
        return bookingMapper.bookingToBookingDto(updatedBooking);
    }

    @Override
    public Collection<BookingDto> getAllBookings() {
        log.info("Получение списка всех бронирований.");
        return bookingStorage.findAll().stream()
                .map(bookingMapper::bookingToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDto getBookingById(long bookingId) {
        Booking booking = bookingStorage.findById(bookingId);
        if (booking == null) {
            throw ExceptionFactory.entityNotFound("Бронирование", bookingId);
        }
        log.info("Бронирование с id {} найдено.", bookingId);
        return bookingMapper.bookingToBookingDto(booking);
    }

    @Override
    public void removeBooking(long bookingId) {
        Booking booking = bookingStorage.findById(bookingId);
        if (booking == null) {
            throw ExceptionFactory.entityNotFound("Бронирование", bookingId);
        }
        bookingStorage.remove(bookingId);
        log.info("Бронирование с id {} удалено.", bookingId);
    }
}
