package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.ExceptionFactory;
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
    public BookingDto addBooking(Long userId, AddBookingDto bookingDto) {
        if (bookingDto.getStartDate() == null || bookingDto.getEndDate() == null) {
            throw new IllegalArgumentException("Необходимо указать дату начала и окончания бронирования.");
        }

        if (bookingDto.getEndDate().isBefore(bookingDto.getStartDate())) {
            throw new IllegalArgumentException("Дата окончания бронирования должна быть позже даты начала.");
        }

        var user = userStorage.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с указанным ID не найден."));
        var item = itemStorage.findById(bookingDto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Вещь с указанным ID не найдена."));

        if (!item.getAvailable() || item.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Вещь недоступна для бронирования или владелец не может бронировать собственную вещь.");
        }

        Booking booking = new Booking();
        booking.setStartDate(bookingDto.getStartDate());
        booking.setEndDate(bookingDto.getEndDate());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingStorage.save(booking);
        return bookingMapper.toBookingDto(savedBooking);
    }


    @Override
    @Transactional
    public BookingDto acknowledgeBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> ExceptionFactory.entityNotFound("Бронирование", bookingId));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw ExceptionFactory.accessDenied("Только владелец вещи может подтвердить или отклонить бронирование.");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingStorage.save(booking);
        return bookingMapper.toBookingDto(updatedBooking);
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> ExceptionFactory.entityNotFound("Бронирование", bookingId));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw ExceptionFactory.accessDenied("Пользователь не является ни арендатором, ни владельцем вещи.");
        }

        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsFromUser(Long userId, BookingState state) {
        userStorage.findById(userId).orElseThrow(() -> ExceptionFactory.userNotFoundException("Пользователь с ID " + userId + " не найден"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingStorage.findAllByBookerId(userId);
                break;
            case PAST:
                bookings = bookingStorage.findAllByBookerIdAndEndDateBefore(userId, now);
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByBookerIdAndStartDateAfter(userId, now);
                break;
            case CURRENT:
                bookings = bookingStorage.findAllByBookerIdAndStartDateBeforeAndEndDateAfter(userId, now, now);
                break;
            case WAITING:
            case REJECTED:
                bookings = bookingStorage.findAllByBookerIdAndStatus(userId, BookingStatus.valueOf(state.name()), Pageable.unpaged());
                break;
            default:
                throw new IllegalArgumentException("Неизвестное состояние: " + state);
        }
        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllOwnerBookings(Long userId, BookingState state) {
        userStorage.findById(userId).orElseThrow(() -> ExceptionFactory.userNotFoundException("Пользователь с ID " + userId + " не найден"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingStorage.findAllByItem_Owner_Id(userId, Pageable.unpaged());
                break;
            case PAST:
                bookings = bookingStorage.findAllByItem_Owner_IdAndEndDateBefore(userId, now, Pageable.unpaged());
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByItem_Owner_IdAndStartDateAfter(userId, now);
                break;
            case CURRENT:
                bookings = bookingStorage.findAllByItem_Owner_IdAndStartDateBeforeAndEndDateAfter(userId, now, now);
                break;
            case WAITING:
            case REJECTED:
                bookings = bookingStorage.findAllByItem_Owner_IdAndStatus(userId, BookingStatus.valueOf(state.name()), Pageable.unpaged());
                break;
            default:
                throw new IllegalArgumentException("Неизвестное состояние: " + state);
        }

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}