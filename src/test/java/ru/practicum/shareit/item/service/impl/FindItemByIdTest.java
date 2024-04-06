package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ru.practicum.shareit.OffsetPageRequest;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.ItemRequestService;

import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import ru.practicum.shareit.booking.service.BookingService;

@ExtendWith(MockitoExtension.class)
class FindItemByIdTest {

    @Mock
    private ItemStorage itemStorage;

    @Mock
    private CommentStorage commentStorage;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private BookingService bookingService;

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;

    @Captor
    private ArgumentCaptor<OffsetPageRequest> offsetPageRequestArgumentCaptor;
    private User owner;

    private long ownerId;

    private User requester;

    private long requesterId;

    private ItemDto itemDto;

    private Item item;

    private long itemId;

    long requestId;

    private Booking booking1;

    private Booking booking2;

    private Booking booking3;

    @BeforeEach
    void setUp() {
        ownerId = 1;
        owner = User.builder()
                .id(ownerId)
                .name("owner")
                .email("owner@email.com")
                .build();
        requesterId = 3;
        requester = User.builder()
                .id(requesterId)
                .name("requester")
                .email("requester@email.com")
                .build();
        requestId = 2;
        itemDto = ItemDto.builder()
                .name("itemDto")
                .description("itemDto description")
                .requestId(requestId)
                .available(true)
                .build();
        itemId = 4;
        item = Item.builder()
                .id(itemId)
                .name("item name")
                .description("item description")
                .owner(owner)
                .available(true)
                .build();
        booking1 = Booking.builder()
                .booker(requester)
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(4))
                .status(BookingStatus.WAITING)
                .build();
        booking2 = Booking.builder()
                .booker(requester)
                .item(item)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(4))
                .status(BookingStatus.WAITING)
                .build();
        booking3 = Booking.builder()
                .booker(requester)
                .item(item)
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(6))
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void findItemById_WhenRequesterIsOwner_ShouldReturnItemWithBookingDates() {
        booking1.setStatus(BookingStatus.APPROVED);
        booking2.setStatus(BookingStatus.APPROVED);
        booking3.setStatus(BookingStatus.APPROVED);

        when(itemStorage.findById(itemId))
                .thenReturn(Optional.of(item));

        when(bookingService.findAllByItemId(itemId))
                .thenReturn(List.of(booking1, booking2, booking3));
        Comment comment = new Comment();
        when(commentStorage.findAllByItemId(itemId))
                .thenReturn(List.of(comment));
        ShortBookingDto shortBookingDto = new ShortBookingDto();
        when(bookingMapper.toShortDto(any()))
                .thenReturn(shortBookingDto);
        when(itemMapper.toGetItemDto(eq(item), any(), any()))
                .thenReturn(new GetItemDto());
        itemService.findItemById(ownerId, itemId);

        verify(itemStorage, times(1)).findById(itemId);
        verify(bookingService, times(1)).findAllByItemId(itemId);
        verify(commentStorage, times(1)).findAllByItemId(item.getId());
        verify(commentMapper, times(1)).toDtoList(any(List.class));



        verify(commentMapper, times(1)).toDtoList(List.of(comment));


    }

}
