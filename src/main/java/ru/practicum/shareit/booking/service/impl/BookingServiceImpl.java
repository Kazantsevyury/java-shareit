package ru.practicum.shareit.booking.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

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

    public List<Booking> findAllByItemIdIn(List<Long> itemIds) {
        return bookingStorage.findAllByItemIdIn(itemIds);
    }


    @Override
    public Booking pureSave(Booking booking) {
        return bookingStorage.save(booking);
    }

    @Override
    public List<Booking> findAllByItemId(Long itemId) {
        return bookingStorage.findAllByItemIdList(itemId);
    }

    public List<Booking> findAllByItemIdAndBookerId(Long itemId, Long userId) {
        return bookingStorage.findAllByItemIdAndBookerId(itemId,userId);
    }

    @Override
    public Booking getBookingByIdAndUserId(Long bookingId, Long userId) {
        Booking booking = findBooking(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new ItemRequestNotFoundException("Бронирование с id '" + bookingId + "' не найдено.");
        }

        return booking;
    }

    @Override
    public Iterable<Booking> findAllByItemOwnerId(Long userId, Pageable pageable) {
        return bookingStorage.findAllByItemOwnerId( userId,  pageable);
    }

    @Override
    public Iterable<Booking> findCurrentBookingsByOwnerId(Long userId, LocalDateTime now, LocalDateTime now1, Pageable pageable) {
        return bookingStorage.findCurrentBookingsByOwnerId( userId,  now,  now1,  pageable);
    }

    @Override
    public Iterable<Booking> findPastBookingsByOwnerId(Long userId, LocalDateTime now, Pageable pageable) {
        return bookingStorage.findPastBookingsByOwnerId( userId,  now,  pageable);
    }

    @Override
    public Iterable<Booking> findFutureBookingsByOwnerId(Long userId, LocalDateTime now, Pageable pageable) {
        return bookingStorage.findFutureBookingsByOwnerId( userId,  now,  pageable);
    }

    @Override
    public Iterable<Booking> findBookingsByOwnerIdAndStatus(Long userId, BookingStatus bookingStatus, Pageable pageable) {
        return bookingStorage.findBookingsByOwnerIdAndStatus( userId,  bookingStatus,  pageable);
    }
    @Override
    public Iterable<Booking> findAllByBookerId(Long bookerId, Pageable pageable){
        return bookingStorage.findAllByBookerId( bookerId,  pageable);

    }

    @Override
    public Iterable<Booking> findCurrentBookingsByBookerId(Long bookerId, LocalDateTime now, LocalDateTime now1, Pageable pageable){
        return bookingStorage.findCurrentBookingsByBookerId( bookerId,  now,  now1,  pageable);

    }

    @Override
    public Iterable<Booking> findPastBookingsByBookerId(Long bookerId, LocalDateTime now, Pageable pageable){
        return bookingStorage.findPastBookingsByBookerId( bookerId,  now,  pageable);
    }

    @Override
    public Iterable<Booking> findFutureBookingsByBookerId(Long bookerId, LocalDateTime now, Pageable pageable){
        return bookingStorage.findFutureBookingsByBookerId( bookerId,  now,  pageable);

    }

    @Override
    public Iterable<Booking> findBookingsByBookerIdAndStatus(Long bookerId, BookingStatus bookingStatus, Pageable pageable){
        return bookingStorage.findBookingsByBookerIdAndStatus( bookerId,  bookingStatus,  pageable);

    }

}
