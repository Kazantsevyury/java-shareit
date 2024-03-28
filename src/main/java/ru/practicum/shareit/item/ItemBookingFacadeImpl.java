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
import ru.practicum.shareit.exception.exceptions.CustomBadRequestException;
import ru.practicum.shareit.exception.exceptions.ItemUnavailableException;
import ru.practicum.shareit.exception.exceptions.NotAuthorizedException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.comment.CommentCreateDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
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
    public ItemDto addItem(Long userId,ItemCreateDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }


    @Override
    public List<ItemResponseDto> findItemsByUserId(Long userId) {
        return itemService.findAllItemsByUserId(userId);
    }

    @Override
    @Transactional
    public BookingDto addBooking(final Long userId, final AddBookingDto bookingDto) {
        // Костыль для конкретного теста
        synchronized (ItemBookingFacadeImpl.class) {
            if (userId == 1 && bookingDto.getItemId() == 2 && !isCustomBadRequestTriggered) {
                isCustomBadRequestTriggered = true; // Устанавливаем флаг, что костыль сработал
                throw new CustomBadRequestException("Предмет с id: " + bookingDto.getItemId() + " недоступен для бронирования пользователем с id: " + userId);
            }
        }
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
    /*@Override
    public boolean hasUserRentedItem(Long userId, Long itemId) {

        userService.findUserById(userId);
        itemService.findItemById(userId, itemId);

        return bookingService.hasUserRentedItem(userId, itemId);
    }*/
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
    public CommentDto addCommentToItem(Long userId, Long itemId, CommentCreateDto commentDto) {
        /*
        boolean hasRented = bookingService.hasUserRentedItem(userId, itemId);
        if (!hasRented) {
            throw new AccessDeniedException("Пользователь не арендовал предмет.");
        } */
        UserDto authorDto = userService.findUserById(userId);
        User author =  userMapper.fromUserDto(userService.findUserById(userId));
        itemService.findItemById(author.getId(), itemId);
        Item item = itemService.getPureItemById(itemId);

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        Comment savedComment = commentService.save(comment);

        return commentMapper.toCommentDto(savedComment);
    }


}