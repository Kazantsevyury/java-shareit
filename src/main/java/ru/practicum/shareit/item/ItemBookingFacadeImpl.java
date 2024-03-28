package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.exceptions.AccessDeniedException;
import ru.practicum.shareit.exception.exceptions.EntityNotFoundException;
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
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemBookingFacadeImpl implements ItemBookingFacade {

    private final ItemService itemService;
    private final BookingService bookingService;
    private final UserService userService;
    private final CommentService commentService;

    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

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
    public BookingResponseDto createBooking(Long userId, AddBookingDto bookingDto) {

        var userDto = userService.findUserById(userId);
        var itemDto = itemService.findItemById(userId, bookingDto.getItemId());


        if (!itemDto.getAvailable()) {
            throw new IllegalArgumentException("Предмет с id: " + bookingDto.getItemId() + " недоступен для бронирования.");
        }

        if (itemDto.getOwner().getId().equals(userId)) {
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
        booking.setItem(itemService.getPureItemById(itemDto.getId()));
        booking.setBooker(userService.getPureUserById(userId));
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingService.simpleSave(booking);

        return bookingMapper.toBookingResponseDto(savedBooking);
    }
    @Override
    public boolean hasUserRentedItem(Long userId, Long itemId) {

        userService.findUserById(userId);
        itemService.findItemById(userId, itemId);

        return bookingService.hasUserRentedItem(userId, itemId);
    }

    @Override
    @Transactional
    public CommentDto addCommentToItem(Long userId, Long itemId, CommentCreateDto commentDto) {
        boolean hasRented = bookingService.hasUserRentedItem(userId, itemId);
        if (!hasRented) {
            throw new AccessDeniedException("Пользователь не арендовал предмет.");
        }
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