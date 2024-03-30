package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.GetBookingState;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.AddCommentDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;

import java.util.List;

public interface ItemBookingFacade {
    ItemDto addItem(Long userId, ItemDto itemDto);

    BookingDto addBooking(final Long userId, final AddBookingDto bookingDto);

    List<GetItemDto> findItemsByUserId(Long userId);
    //boolean hasUserRentedItem(Long userId, Long itemId);

    CommentDto addCommentToItem(Long userId, Long itemId, AddCommentDto commentDto);

    BookingDto acknowledgeBooking(Long userId, Long bookingId, Boolean approved);

    List<BookingDto> getAllBookingsFromUser(Long userId, GetBookingState state);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getAllOwnerBookings(Long userId, GetBookingState state);
}
