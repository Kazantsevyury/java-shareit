package ru.practicum.shareit.booking.service;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.GetBookingState;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.shared.OffsetPageRequest;
import ru.practicum.shareit.shared.exception.ItemUnavailableException;
import ru.practicum.shareit.shared.exception.NotAuthorizedException;
import ru.practicum.shareit.shared.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto addBooking(final Long userId, final AddBookingDto bookingDto) {
        final User user = findUser(userId);
        final Item item = itemStorage.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id '" + bookingDto.getItemId() + "' не найдена."));
        checkItemAvailability(item);
        if (item.getOwner().getId().equals(userId)) {
            throw new NotAuthorizedException("Вещь с id '" + item.getId() +
                    "' уже принадлежит пользователю с id '" + userId + "'.");
        }
        final Booking booking = Booking.builder()
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
        final Booking savedBooking = bookingStorage.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto acknowledgeBooking(final Long userId, final Long bookingId, final Boolean approved) {
        findUser(userId);
        final Booking booking = findBooking(bookingId);
        final Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotAuthorizedException("Пользователь с id '" + userId +
                    "' не является владельцем вещи с id '" + item.getId() + "'.");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ItemUnavailableException("Текущий статус бронирования не позволяет сделать подтверждение.");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto getBookingById(final Long userId, final Long bookingId) {
        findUser(userId);
        final Booking booking = findBooking(bookingId);
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return bookingMapper.toDto(booking);
        } else {
            throw new NotAuthorizedException("У пользователя с id '" + userId + "' нет прав для доступа к бронированию с" +
                    " id '" + bookingId + "'.");
        }
    }

    @Override
    public List<BookingDto> getAllBookingsFromUser(final Long userId, final GetBookingState state, Long from,
                                                   Integer size, boolean isOwner) {
        findUser(userId);
        Iterable<Booking> result = new ArrayList<>();
        if (isOwner) {
            result = getBookingFromOwner(userId, state, from, size, result);
        } else {
            result = getBookingFromUser(userId, state, from, size, result);
        }
        return bookingMapper.toDtoList(Lists.newArrayList(result));
    }

    private Iterable<Booking> getBookingFromOwner(Long userId, GetBookingState state, Long from, Integer size, Iterable<Booking> result) {
        OffsetPageRequest pageRequest = OffsetPageRequest.of(from, size);
        result = getAllSortedBookingsFromUser(state, result, userId, pageRequest);
        return result;
    }

    private Iterable<Booking> getBookingFromUser(Long userId, GetBookingState state, Long from, Integer size, Iterable<Booking> result) {
        OffsetPageRequest pageRequest = OffsetPageRequest.of(from, size);
        result = getAllSortedBookingsFromBooker(state, result, userId, pageRequest);
        return result;
    }

    private Iterable<Booking> getAllSortedBookingsFromUser(final GetBookingState state, Iterable<Booking> result,
                                                           final Long userId, Pageable pageable) {
        switch (state) {
            case ALL:
                result = bookingStorage.findAllByItemOwnerId(userId, pageable);
                break;
            case CURRENT:
                result = bookingStorage.findCurrentBookingsByOwnerId(userId, LocalDateTime.now(), LocalDateTime.now(),
                        pageable);
                break;
            case PAST:
                result = bookingStorage.findPastBookingsByOwnerId(userId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                result = bookingStorage.findFutureBookingsByOwnerId(userId, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                result = bookingStorage.findBookingsByOwnerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                result = bookingStorage.findBookingsByOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
        }
        return result;
    }

    private Iterable<Booking> getAllSortedBookingsFromBooker(final GetBookingState state, Iterable<Booking> result,
                                                             final Long bookerId, Pageable pageable) {
        switch (state) {
            case ALL:
                result = bookingStorage.findAllByBookerId(bookerId, pageable);
                break;
            case CURRENT:
                result = bookingStorage.findCurrentBookingsByBookerId(bookerId, LocalDateTime.now(), LocalDateTime.now(),
                        pageable);
                break;
            case PAST:
                result = bookingStorage.findPastBookingsByBookerId(bookerId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                result = bookingStorage.findFutureBookingsByBookerId(bookerId, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                result = bookingStorage.findBookingsByBookerIdAndStatus(bookerId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                result = bookingStorage.findBookingsByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, pageable);
                break;
        }
        return result;
    }

    private User findUser(final Long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id '" + userId + "' не найден."));
    }

    private Booking findBooking(final Long bookingId) {
        return bookingStorage.findBookingById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id '" + bookingId + "' не найдено."));
    }

    private void checkItemAvailability(final Item item) {
        if (!item.getAvailable()) {
            throw new ItemUnavailableException("Вещь недоступна для бронирования.");
        }
    }
}
