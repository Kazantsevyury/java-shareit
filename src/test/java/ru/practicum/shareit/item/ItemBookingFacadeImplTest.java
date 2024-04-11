package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.OffsetPageRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.GetBookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.exceptions.BookingOwnershipException;
import ru.practicum.shareit.exception.exceptions.ItemUnavailableException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemBookingFacadeImplTest {

    @Mock
    private ItemServiceImpl itemService;
    @Mock
    private BookingServiceImpl bookingService;
    @Mock
    private UserService userService;
    @Mock
    private CommentService commentService;

    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private ItemBookingFacadeImpl itemBookingFacade;

    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    private long userId;
    private long bookingId;
    private long itemId;
    private Item item;
    private User itemOwner;
    private User booker;
    private Booking booking;

    void init() {
        userId = 1;

        bookingId = 2;
        itemId = 3;
        itemOwner = User.builder()
                .id(5L)
                .name("name")
                .email("email@mail.com")
                .build();
        item = Item.builder()
                .id(itemId)
                .name("name")
                .description("description")
                .available(true)
                .owner(itemOwner)
                .build();
        booker = User.builder()
                .id(userId)
                .build();
        booking = Booking.builder()
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(4))
                .item(item)
                .booker(booker)
                .build();
    }

    @Test
    void addBooking_ItemAndUserFound_ShouldReturnBookingDto() {
        init();
        AddBookingDto addBookingDto = AddBookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(4))
                .build();
        User user = new User();
        when(userService.getPureUserById(userId)).thenReturn(user);
        when(itemService.getPureItemById(itemId)).thenReturn(item);

        when(bookingService.pureSave(any(Booking.class))).thenReturn(booking);

        itemBookingFacade.addBooking(userId, addBookingDto);

        verify(userService, times(1)).getPureUserById(userId);
        verify(itemService, times(1)).getPureItemById(itemId);
        verify(bookingService, times(1)).pureSave(bookingArgumentCaptor.capture());
        Booking capturedBooking = bookingArgumentCaptor.getValue();

        assertThat(capturedBooking.getItem(), is(item));
        assertThat(capturedBooking.getBooker(), is(user));
        assertThat(capturedBooking.getStatus(), is(BookingStatus.WAITING));
        assertThat(capturedBooking.getStart(), is(addBookingDto.getStart()));
        assertThat(capturedBooking.getEnd(), is(addBookingDto.getEnd()));

        verify(bookingMapper, times(1)).toDto(any(Booking.class));
    }

    @Test
    void addItem_ItemProvided_ShouldReturnItemDto() {
        init();
        Long userId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Bicycle");
        itemDto.setDescription("Mountain Bike");
        itemDto.setAvailable(true);

        Item item = new Item();
        item.setId(1L);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(new User());

        ItemDto expectedItemDto = new ItemDto();
        expectedItemDto.setId(item.getId());
        expectedItemDto.setName(item.getName());
        expectedItemDto.setDescription(item.getDescription());
        expectedItemDto.setAvailable(item.getAvailable());

        when(itemService.addItem(eq(userId), any(ItemDto.class))).thenReturn(expectedItemDto);

        ItemDto resultItemDto = itemBookingFacade.addItem(userId, itemDto);

        verify(itemService, times(1)).addItem(eq(userId), any(ItemDto.class));
        assertThat(resultItemDto.getId(), is(expectedItemDto.getId()));
        assertThat(resultItemDto.getName(), is(expectedItemDto.getName()));
        assertThat(resultItemDto.getDescription(), is(expectedItemDto.getDescription()));
        assertThat(resultItemDto.getAvailable(), is(expectedItemDto.getAvailable()));
    }

    @Test
    void testAddItemWhenInputIsValidThenReturnItemDto() {
        // Arrange
        Long userId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Bicycle");
        itemDto.setDescription("Mountain Bike");
        itemDto.setAvailable(true);

        ItemDto expectedItemDto = new ItemDto();
        expectedItemDto.setId(1L);
        expectedItemDto.setName("Bicycle");
        expectedItemDto.setDescription("Mountain Bike");
        expectedItemDto.setAvailable(true);

        when(itemService.addItem(eq(userId), any(ItemDto.class))).thenReturn(expectedItemDto);

        // Act
        ItemDto resultItemDto = itemBookingFacade.addItem(userId, itemDto);

        // Assert
        verify(itemService, times(1)).addItem(eq(userId), any(ItemDto.class));
        assertEquals(expectedItemDto, resultItemDto);
    }

    @Test
    void testGetAllBookingsFromUserWhenInputIsValidThenReturnListOfBookingDto() {
        // Arrange
        Long userId = 1L;
        GetBookingState state = GetBookingState.ALL;
        Long from = 0L;
        Integer size = 10;
        boolean isOwner = true;
        List<BookingDto> expectedBookingDtoList = new ArrayList<>();

        when(userService.findUserById(userId)).thenReturn(new UserDto());
        when(bookingService.findAllByItemOwnerId(eq(userId), any(OffsetPageRequest.class))).thenReturn(new ArrayList<>());
        when(bookingMapper.toDtoList(anyList())).thenReturn(expectedBookingDtoList);

        // Act
        List<BookingDto> resultBookingDtoList = itemBookingFacade.getAllBookingsFromUser(userId, state, from, size, isOwner);

        // Assert
        verify(bookingService, times(1)).findAllByItemOwnerId(eq(userId), any(OffsetPageRequest.class));
        assertEquals(expectedBookingDtoList, resultBookingDtoList);
    }

    @Test
    void testAcknowledgeBookingWhenNotOwnerThenBookingOwnershipException() {
        // Arrange
        init();
        Boolean approved = true;
        User otherUser = User.builder().id(6L).build();
        item.setOwner(otherUser);
        when(userService.findUserById(userId)).thenReturn(new UserDto());
        when(bookingService.findBooking(bookingId)).thenReturn(booking);

        // Act & Assert
        assertThrows(BookingOwnershipException.class, () -> itemBookingFacade.acknowledgeBooking(userId, bookingId, approved));
    }

    @Test
    void testAcknowledgeBookingWhenItemNotWaitingThenThrowException() {
        // Arrange
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;

        User owner = User.builder().id(userId).build();
        Item item = Item.builder().id(1L).owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(owner)
                .status(BookingStatus.APPROVED)  // Status is not WAITING
                .build();

        when(userService.findUserById(userId)).thenReturn(new UserDto());
        when(bookingService.findBooking(bookingId)).thenReturn(booking);

        // Act & Assert
        assertThrows(ItemUnavailableException.class, () -> {
            itemBookingFacade.acknowledgeBooking(userId, bookingId, approved);
        });

        // Verify
        verify(userService).findUserById(userId);
        verify(bookingService).findBooking(bookingId);
        verifyNoMoreInteractions(bookingMapper);  // Ensure no mapping occurs since exception is thrown
    }

    @Test
    void testAcknowledgeBookingApprovesBooking() {
        // Arrange
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;

        User owner = User.builder().id(userId).build();
        Item item = Item.builder().id(1L).owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(owner)
                .status(BookingStatus.WAITING)
                .build();

        BookingDto expectedDto = new BookingDto(); // Assuming you have a corresponding DTO setup
        when(userService.findUserById(userId)).thenReturn(new UserDto());
        when(bookingService.findBooking(bookingId)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(expectedDto);

        // Act
        BookingDto resultDto = itemBookingFacade.acknowledgeBooking(userId, bookingId, approved);

        // Assert
        assertEquals(BookingStatus.APPROVED, booking.getStatus(), "Booking status should be APPROVED");
        assertNotNull(resultDto, "Returned BookingDto should not be null");
        assertSame(expectedDto, resultDto, "Returned DTO should match the expected DTO");

        // Verify
        verify(bookingMapper).toDto(booking);
        verify(userService).findUserById(userId);
        verify(bookingService).findBooking(bookingId);
    }

    @Test
    void testAcknowledgeBookingRejectsBooking() {
        // Arrange
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = false;

        User owner = User.builder().id(userId).build();
        Item item = Item.builder().id(1L).owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(owner)
                .status(BookingStatus.WAITING)
                .build();

        BookingDto expectedDto = new BookingDto(); // Assuming you have a corresponding DTO setup
        when(userService.findUserById(userId)).thenReturn(new UserDto());
        when(bookingService.findBooking(bookingId)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(expectedDto);

        // Act
        BookingDto resultDto = itemBookingFacade.acknowledgeBooking(userId, bookingId, approved);

        // Assert
        assertEquals(BookingStatus.REJECTED, booking.getStatus(), "Booking status should be REJECTED");
        assertNotNull(resultDto, "Returned BookingDto should not be null");
        assertSame(expectedDto, resultDto, "Returned DTO should match the expected DTO");

        // Verify
        verify(bookingMapper).toDto(booking);
        verify(userService).findUserById(userId);
        verify(bookingService).findBooking(bookingId);
    }

    @Test
    void testReturnBookingDtoFromAcknowledgeBooking() {
        // Arrange
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = true; // or false, doesn't matter for this test

        User owner = User.builder().id(userId).build();
        Item item = Item.builder().id(1L).owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(owner)
                .status(BookingStatus.WAITING)
                .build();
        BookingDto expectedDto = new BookingDto(); // Assuming you have a corresponding DTO setup

        when(userService.findUserById(userId)).thenReturn(new UserDto());
        when(bookingService.findBooking(bookingId)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(expectedDto);

        // Act
        BookingDto resultDto = itemBookingFacade.acknowledgeBooking(userId, bookingId, approved);

        // Assert
        assertSame(expectedDto, resultDto, "Returned DTO should match the expected DTO");

        // Verify
        verify(bookingMapper).toDto(booking);
        verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void testGetBookingFromUser() {
        // Arrange
        Long userId = 1L;
        GetBookingState state = GetBookingState.ALL;
        Long from = 0L;
        Integer size = 10;
        List<Booking> expectedBookings = Arrays.asList(new Booking(), new Booking());

        when(bookingService.findAllByBookerId(eq(userId), any(Pageable.class))).thenReturn(expectedBookings);

        // Act
        Iterable<Booking> result = itemBookingFacade.getBookingFromUser(userId, state, from, size, new ArrayList<>());

        // Assert
        assertIterableEquals(expectedBookings, result, "The returned bookings should match the expected list.");

        // Verify
        verify(bookingService).findAllByBookerId(eq(userId), any(Pageable.class));
    }

    @Test
    void testGetAllSortedBookingsFromBooker() {
        // Arrange
        Long userId = 1L;
        GetBookingState state = GetBookingState.ALL;
        Long from = 0L;
        Integer size = 10;
        Iterable<Booking> initialResult = Collections.emptyList();
        Pageable pageable = PageRequest.of(Math.toIntExact(from), size);

        // Act
        when(bookingService.findAllByBookerId(eq(userId), eq(pageable))).thenReturn(initialResult);
        Iterable<Booking> result = itemBookingFacade.getAllSortedBookingsFromBooker(state, initialResult, userId, pageable);

        // Assert
        assertSame(initialResult, result);
        verify(bookingService).findAllByBookerId(eq(userId), eq(pageable));
    }
}