package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemServiceImpolTestgetItemsWithBookingsAndComments {

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetItemsWithBookingsAndComments_EmptyBookings() {
        List<Item> items = Arrays.asList(new Item(1L), new Item(2L));
        List<Booking> bookings = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();

        when(itemMapper.toWithBookingsDtoList(items)).thenReturn(Arrays.asList(
                new GetItemDto(1L, new ArrayList<>()),
                new GetItemDto(2L, new ArrayList<>())
        ));

        List<GetItemDto> result = itemService.getItemsWithBookingsAndComments(items, bookings, comments);

        assertEquals(2, result.size());
        verify(itemMapper).toWithBookingsDtoList(items);
        verifyNoInteractions(commentMapper); // No comments to process
    }

    @Test
    public void testGetItemsWithBookingsAndComments_WithBookingsAndComments() {
        List<Item> items = Arrays.asList(new Item(1L), new Item(2L));
        List<Booking> bookings = Arrays.asList(
                new Booking(1L, new Item(1L)),
                new Booking(2L, new Item(2L))
        );
        List<Comment> comments = Arrays.asList(
                new Comment(1L, "Good", new Item(1L)),
                new Comment(2L, "Bad", new Item(2L))
        );


        when(commentMapper.toDtoList(anyList())).thenReturn(Arrays.asList(new CommentDto("Good"), new CommentDto("Bad")));

        List<GetItemDto> result = itemService.getItemsWithBookingsAndComments(items, bookings, comments);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertFalse(result.get(0).getComments().isEmpty());
        assertFalse(result.get(1).getComments().isEmpty());
        verify(itemMapper, times(2)).toWithBookingsDtoList(any(Item.class), anyList());
        verify(commentMapper, times(2)).toDtoList(anyList());
    }
}
