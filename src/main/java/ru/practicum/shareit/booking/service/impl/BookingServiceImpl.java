package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.ExceptionFactory;
import ru.practicum.shareit.exception.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exception.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserStorage;

import javax.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingResponseDto addBooking(Long userId, AddBookingDto bookingDto) {
        var user = userStorage.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден по id: " + userId));

        var item = itemStorage.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Предмет не найден по id: " + bookingDto.getItemId()));

        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Предмет с id: " + bookingDto.getItemId() + " недоступен для бронирования.");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Владелец не может бронировать свой предмет.");
        }

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new IllegalArgumentException("Дата начала и окончания не должны быть пустыми.");
        }

        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Дата начала не должна быть в прошлом.");
        }

        if (bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new IllegalArgumentException("Дата начала и окончания не могут совпадать.");
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new IllegalArgumentException("Дата начала должна быть раньше даты окончания.");
        }

        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingStorage.save(booking);

        return bookingMapper.toBookingResponseDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto acknowledgeBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Бронирование не найдено по id: " + bookingId));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Только владелец предмета может подтвердить или отклонить бронирование.");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingStorage.save(booking);

        return bookingMapper.toBookingResponseDto(updatedBooking);
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Бронирование не найдено по id: " + bookingId));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Пользователь не является ни арендатором, ни владельцем предмета.");
        }

        return bookingMapper.toBookingResponseDto(booking);
    }

    @Override
        public List<BookingResponseDto> getAllBookingsFromUser(Long userId, BookingState state) {
        userStorage.findById(userId).orElseThrow(() -> ExceptionFactory.userNotFoundException("Пользователь с ID " + userId + " не найден"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingStorage.findAllByBookerId(userId);
                break;
            case PAST:
                bookings = bookingStorage.findAllByBookerIdAndEndBefore(userId, now);
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByBookerIdAndStartAfter(userId, now);
                break;
            case CURRENT:
                bookings = bookingStorage.findAllByBookerIdAndStartBeforeAndEndAfter(userId, now, now);
                break;
            case WAITING:
            case REJECTED:
                bookings = bookingStorage.findAllByBookerIdAndStatus(userId, BookingStatus.valueOf(state.name()), Pageable.unpaged());
                break;
            default:
                throw new IllegalArgumentException("Неизвестное состояние: " + state);
        }
            return bookings.stream()
                    .map(booking -> bookingMapper.toBookingResponseDto(booking))
                    .collect(Collectors.toList());
    }

    @Override
        public List<BookingResponseDto> getAllOwnerBookings(Long userId, BookingState state) {
        userStorage.findById(userId).orElseThrow(() -> ExceptionFactory.userNotFoundException("Пользователь с ID " + userId + " не найден"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingStorage.findAllByItem_Owner_Id(userId, Pageable.unpaged());
                break;
            case PAST:
                bookings = bookingStorage.findAllByItem_Owner_IdAndEndBefore(userId, now, Pageable.unpaged());
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByItem_Owner_IdAndStartAfter(userId, now);
                break;
            case CURRENT:
                bookings = bookingStorage.findAllByItem_Owner_IdAndStartBeforeAndEndAfter(userId, now, now);
                break;
            case WAITING:
            case REJECTED:
                bookings = bookingStorage.findAllByItem_Owner_IdAndStatus(userId, BookingStatus.valueOf(state.name()), Pageable.unpaged());
                break;
            default:
                throw new IllegalArgumentException("Неизвестное состояние: " + state);
        }

            return bookings.stream()
                    .map(booking -> bookingMapper.toBookingResponseDto(booking))
                    .collect(Collectors.toList());
    }
}