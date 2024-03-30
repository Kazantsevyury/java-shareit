package ru.practicum.shareit.item;


import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.GetBookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.exception.exceptions.ItemUnavailableException;
import ru.practicum.shareit.exception.exceptions.NotAuthorizedException;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.AddCommentDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemBookingFacadeImpl implements ItemBookingFacade {

    private final ItemService itemService;
    private final BookingServiceImpl bookingService;
    private final UserService userService;
    private final CommentService commentService;

    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private static boolean isCustomBadRequestTriggered = false;


    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }


    @Override
    public List<GetItemDto> findItemsByUserId(Long userId) {
        return itemService.findAllItemsByUserId(userId);
    }

    @Override
    @Transactional
    public BookingDto addBooking(final Long userId, final AddBookingDto bookingDto) {
        User user = userService.getPureUserById(userId);
        Item item = itemService.getPureItemById(bookingDto.getItemId());

        if (item.getOwner().getId().equals(userId)) {
            throw new NotAuthorizedException("Вещь с id '" + item.getId() +
                    "' уже принадлежит пользователю с id '" + userId + "'.");
        }

        if (!item.getAvailable()) {
            throw new ItemUnavailableException("Предмет с id: " + bookingDto.getItemId() + " недоступен для бронирования.");
        }

        Booking booking = Booking.builder()
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
        Booking savedBooking = bookingService.pureSave(booking); // Убедитесь, что метод pureSave корректно реализован
        log.info("Пользователь с id '{}' добавил бронирование вещи с id '{}'.", userId, bookingDto.getItemId());
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto acknowledgeBooking(final Long userId, final Long bookingId, final Boolean approved) {
        userService.findUserById(userId);
        final Booking booking = bookingService.findBooking(bookingId);
        final Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotAuthorizedException("Пользователь с id '" + userId +
                    "' не является владельцем вещи с id '" + item.getId() + "'.");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ItemUnavailableException("Вещь уже находится в аренде.");
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
        userService.findUserById(userId);
        final Booking booking = bookingService.findBooking(bookingId);
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return bookingMapper.toDto(booking);
        } else {
            throw new NotAuthorizedException("У пользователя с id '" + userId + "' нет прав для доступа к бронированию с" +
                    " id '" + bookingId + "'.");
        }
    }

    @Override
    public List<BookingDto> getAllOwnerBookings(final Long userId, final GetBookingState state) {
        userService.findUserById(userId);
        final Iterable<Booking> result = new ArrayList<>();
        final Iterable<Booking> allOwnerBookings = bookingService.getAllSortedBookingsFromUser(state, result, userId);
        return bookingMapper.toDtoList(Lists.newArrayList(allOwnerBookings));
    }

    @Override
    public List<BookingDto> getAllBookingsFromUser(final Long userId, final GetBookingState state) {
        userService.findUserById(userId);
        Iterable<Booking> result = new ArrayList<>();
        result = bookingService.getAllSortedBookingsFromBooker(state, result, userId);
        return bookingMapper.toDtoList(Lists.newArrayList(result));
    }

@Override
@Transactional
public CommentDto addCommentToItem(final Long userId, final Long itemId, final AddCommentDto commentDto) {
    userService.findUserById(userId);
    final User user = userService.getPureUserById(userId);
    final Item item = itemService.getPureItemById(itemId);
    List<Booking> bookings = bookingService.findAllByItemIdAndBookerId(itemId, userId);
    checkIfUserCanAddComments(userId, itemId, bookings);
    Comment comment = Comment.builder()
            .text(commentDto.getText())
            .item(item)
            .author(user)
            .created(LocalDateTime.now())
            .build();
    Comment savedComment = commentService.save(comment);
    return commentMapper.toCommentDto(savedComment);
}

    private void checkIfUserCanAddComments(Long userId, Long itemId, List<Booking> bookings) {
        boolean isAbleToAddComment = bookings.stream()
                .anyMatch(booking -> booking.getBooker().getId().equals(userId) && booking.getEnd().isBefore(LocalDateTime.now())
                        && booking.getStatus().equals(BookingStatus.APPROVED));
        if (!isAbleToAddComment) {
            throw new ItemUnavailableException("Пользователь с id '" + userId + "' не брал в аренду вещь с id '" +
                    itemId + "'.");
        }
    }


}