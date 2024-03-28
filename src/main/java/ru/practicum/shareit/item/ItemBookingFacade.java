package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.comment.CommentCreateDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;

import java.util.List;

public interface ItemBookingFacade {
    ItemDto addItem(Long userId,ItemCreateDto itemDto);
    BookingResponseDto createBooking(Long userId, AddBookingDto bookingDto);
    List<ItemResponseDto> findItemsByUserId(Long userId);
    boolean hasUserRentedItem(Long userId, Long itemId);
    CommentDto addCommentToItem(Long userId, Long itemId, CommentCreateDto commentDto);
}
