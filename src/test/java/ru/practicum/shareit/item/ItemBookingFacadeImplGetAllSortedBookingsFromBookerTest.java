package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.GetBookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.impl.CommentServiceImpl;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class ItemBookingFacadeImplGetAllSortedBookingsFromBookerTest{
        @Mock
        private BookingServiceImpl bookingService;
    @Mock
    private CommentServiceImpl commentService;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemServiceImpl itemService;
        @InjectMocks
        private ItemBookingFacadeImpl itemBookingFacade;

        private Long bookerId;
        private Pageable pageable;
        private LocalDateTime now;
        private LocalDateTime testTime;


    @Test
    void whenGetAllSortedBookingsFromBookerWithAllStateThenReturnAllBookings() {
        openMocks(this); // Инициализация моков
        Long bookerId = 1L;
        Pageable pageable = PageRequest.of(0, 5);

        User user = new User(1L, "John Doe", "john.doe@example.com");
        Item item = new Item(1L, "Drill", "Powerful electric drill", true, user, null);
        Booking booking1 = new Booking(1L, item, user, BookingStatus.WAITING, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        Booking booking2 = new Booking(2L, item, user, BookingStatus.APPROVED, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1));

        List<Booking> expectedBookings = List.of(booking1, booking2);

        when(bookingService.findAllByBookerId(bookerId, pageable)).thenReturn(expectedBookings);

        Iterable<Booking> result = itemBookingFacade.getAllSortedBookingsFromBooker(GetBookingState.ALL, null, bookerId, pageable);

        assertNotNull(result);
        assertEquals(expectedBookings, result);
        verify(bookingService).findAllByBookerId(bookerId, pageable);
    }


}