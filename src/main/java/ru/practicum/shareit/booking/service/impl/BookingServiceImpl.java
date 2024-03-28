package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingStorage;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;

import javax.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    @Lazy
    private final UserService userService;


    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;


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
        userService.findUserById(userId);

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
        userService.findUserById(userId);

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

    @Override
    public final Booking simpleSave(Booking booking){
        if (booking == null) {
            log.error("Объект booking равен null");
            return null;
        }
        log.info("Сохраняем booking: {}", booking);
        try {
            return bookingStorage.save(booking);
        } catch (Exception e) {
            log.error("Ошибка при сохранении booking", e);
            throw e;
        }
    }

    @Override
    public boolean hasUserRentedItem(Long userId, Long itemId){
        return bookingStorage.hasUserRentedItem(userId,itemId);
    }

}