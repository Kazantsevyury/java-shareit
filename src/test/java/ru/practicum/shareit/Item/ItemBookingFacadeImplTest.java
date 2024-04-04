package ru.practicum.shareit.Item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.ItemBookingFacadeImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;


import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ItemBookingFacadeImplTest {

    @Mock
    private ItemService itemService;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private BookingServiceImpl bookingService;

    @Mock
    private UserService userService;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private ItemBookingFacadeImpl itemBookingFacade;

    @BeforeEach
    void setUp() {
    }

    @Test
    void addItem_ShouldCallItemService() {
        Long userId = 1L;
        ItemDto itemDto = new ItemDto();
        when(itemService.addItem(anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        itemBookingFacade.addItem(userId, itemDto);

        verify(itemService).addItem(userId, itemDto);
    }

}
