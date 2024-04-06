package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemStorage itemStorage;

    @Mock
    private CommentStorage commentStorage;

    @Mock
    private BookingService bookingService;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private List<Item> items;
    private List<Booking> bookings;
    private List<Comment> comments;

    @BeforeEach
    public void setUp() {
        items = new ArrayList<>();
        bookings = new ArrayList<>();
        comments = new ArrayList<>();
    }

    @Test
    public void testGetItemsWithBookingsAndCommentsWhenListsNotEmptyThenReturnList() {
        Mockito.lenient().when(itemMapper.toWithBookingsDtoList(items)).thenReturn(new ArrayList<>());
        Mockito.lenient().when(bookingMapper.toShortDto(null)).thenReturn(null);
        Mockito.lenient().when(commentMapper.toDtoList(comments)).thenReturn(new ArrayList<>());

        List<GetItemDto> result = itemService.getItemsWithBookingsAndComments(items, bookings, comments);

        assertEquals(new ArrayList<>(), result);
    }

    @Test
    public void testGetItemsWithBookingsAndCommentsWhenBookingsAndCommentsEmptyThenReturnList() {
        items.add(new Item());

        Mockito.lenient().when(itemMapper.toWithBookingsDtoList(items)).thenReturn(new ArrayList<>());
        Mockito.lenient().when(bookingMapper.toShortDto(null)).thenReturn(null);
        Mockito.lenient().when(commentMapper.toDtoList(comments)).thenReturn(new ArrayList<>());

        List<GetItemDto> result = itemService.getItemsWithBookingsAndComments(items, bookings, comments);

        assertEquals(new ArrayList<>(), result);
    }

    @Test
    public void testGetItemsWithBookingsAndCommentsWhenItemsEmptyThenReturnEmptyList() {
        Mockito.lenient().when(itemMapper.toWithBookingsDtoList(items)).thenReturn(new ArrayList<>());
        Mockito.lenient().when(bookingMapper.toShortDto(null)).thenReturn(null);
        Mockito.lenient().when(commentMapper.toDtoList(comments)).thenReturn(new ArrayList<>());

        List<GetItemDto> result = itemService.getItemsWithBookingsAndComments(items, bookings, comments);

        assertTrue(result.isEmpty());
    }
}