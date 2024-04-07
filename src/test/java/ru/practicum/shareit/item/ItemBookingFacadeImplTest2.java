package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.exception.exceptions.NotAuthorizedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemBookingFacadeImplTest2 {

    @Mock
    private ItemService itemService;
    @Mock
    private BookingServiceImpl bookingService;
    @Mock
    private UserService userService;
    @Mock
    private CommentService commentService;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private ItemBookingFacadeImpl itemBookingFacade;



    @Test
    void getBookingByIdWithNoRightsToAccessThrowsNotAuthorized() {
        Long userId = 1L;
        Long bookingId = 1L;
        Long wrongUserId = 3L;  // ID, не связанный ни с бронированием, ни с владельцем
        LocalDateTime now = LocalDateTime.now();

        UserDto userDto = new UserDto(wrongUserId, "Other User", "other@example.com");
        User booker = new User(2L, "Booker Name", "booker@example.com");
        User owner = new User(4L, "Owner Name", "owner@example.com");
        Item item = new Item(1L, "Item Name", "Description", true, owner, null);
        Booking booking = new Booking(bookingId, item, booker, BookingStatus.APPROVED, now.minusDays(1), now.plusDays(1));

        when(userService.findUserById(wrongUserId)).thenReturn(userDto);
        when(bookingService.findBooking(bookingId)).thenReturn(booking);

        Exception exception = assertThrows(NotAuthorizedException.class, () -> {
            itemBookingFacade.getBookingById(wrongUserId, bookingId);
        });

        assertEquals("У пользователя с id '" + wrongUserId + "' нет прав для доступа к бронированию с id '" + bookingId + "'.", exception.getMessage());
        verify(userService).findUserById(wrongUserId);
        verify(bookingService).findBooking(bookingId);
    }

    @Test
    void getBookingByIdWhenUserIsNeitherBookerNorOwnerThrowsNotAuthorized() {
        Long userId = 5L; // Пользователь, который не связан с бронированием
        Long bookingId = 2L;
        LocalDateTime now = LocalDateTime.now();

        UserDto userDto = new UserDto(userId, "Unrelated User", "unrelated@example.com");
        User booker = new User(6L, "Booker Name", "booker@example.com");
        User owner = new User(7L, "Owner Name", "owner@example.com");
        Item item = new Item(3L, "Item Name", "Description", true, owner, null);
        Booking booking = new Booking(bookingId, item, booker, BookingStatus.APPROVED, now.minusDays(1), now.plusDays(1));

        when(userService.findUserById(userId)).thenReturn(userDto);
        when(bookingService.findBooking(bookingId)).thenReturn(booking);

        Exception exception = assertThrows(NotAuthorizedException.class, () -> {
            itemBookingFacade.getBookingById(userId, bookingId);
        });

        assertEquals("У пользователя с id '" + userId + "' нет прав для доступа к бронированию с id '" + bookingId + "'.", exception.getMessage());
        verify(userService).findUserById(userId);
        verify(bookingService).findBooking(bookingId);
    }

    @Test
    void getBookingByIdWithValidData() {
        Long userId = 1L;
        Long bookingId = 1L;
        LocalDateTime now = LocalDateTime.now();

        // Создание объекта UserDto, поскольку метод ожидается возвращать UserDto
        UserDto userDto = new UserDto(userId, "User Name", "user@example.com");

        User itemOwner = new User(2L, "Owner Name", "owner@example.com");
        Item item = new Item(1L, "Item Name", "Description", true, itemOwner, null);

        // Правильное создание объекта Booking
        Booking booking = new Booking(bookingId, item, new User(userId, "User Name", "user@example.com"),
                BookingStatus.APPROVED, now.minusDays(1), now.plusDays(1));

        // Использование userDto в качестве возвращаемого значения
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(bookingService.findBooking(bookingId)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(new BookingDto());

        BookingDto result = itemBookingFacade.getBookingById(userId, bookingId);

        assertNotNull(result);
        verify(userService).findUserById(userId);
        verify(bookingService).findBooking(bookingId);
        verify(bookingMapper).toDto(booking);
    }




}

