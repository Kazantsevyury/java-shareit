package ru.practicum.shareit.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exception.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @Mock
    private BookingStorage bookingStorage;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking;
    private User owner;
    private User booker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        owner = new User();
        owner.setId(1L);
        booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
    }

    @Test
    void getBookingByIdAndUserId_WhenBooker_Success() {
        when(bookingStorage.findBookingById(anyLong())).thenReturn(java.util.Optional.of(booking));

        Booking result = bookingService.getBookingByIdAndUserId(1L, 2L);

        assertNotNull(result);
        assertEquals(2L, result.getBooker().getId());
    }

    @Test
    void getBookingByIdAndUserId_WhenOwner_Success() {
        when(bookingStorage.findBookingById(anyLong())).thenReturn(java.util.Optional.of(booking));

        Booking result = bookingService.getBookingByIdAndUserId(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getItem().getOwner().getId());
    }

    @Test
    void getBookingByIdAndUserId_WhenNeitherOwnerNorBooker_ThrowsException() {
        when(bookingStorage.findBookingById(anyLong())).thenReturn(java.util.Optional.of(booking));

        Exception exception = assertThrows(ItemRequestNotFoundException.class, () -> bookingService.getBookingByIdAndUserId(1L, 3L));

        String expectedMessage = "Бронирование с id '1' не найдено.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
    @Test
    void pureSave_ShouldSaveBooking_Success() {
        when(bookingStorage.save(any(Booking.class))).thenReturn(booking);

        Booking savedBooking = bookingService.pureSave(booking);

        assertNotNull(savedBooking);
        verify(bookingStorage, times(1)).save(any(Booking.class));
    }
    @Test
    void findBooking_ExistingId_ReturnsBooking() {
        when(bookingStorage.findBookingById(anyLong())).thenReturn(java.util.Optional.of(booking));

        Booking foundBooking = bookingService.findBooking(1L);

        assertNotNull(foundBooking);
        assertEquals(1L, foundBooking.getId());
    }

    @Test
    void findBooking_NonExistingId_ThrowsException() {
        when(bookingStorage.findBookingById(anyLong())).thenReturn(java.util.Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.findBooking(1L));
    }
    @Test
    void findAllByItemIdIn_ShouldReturnListOfBookings_WhenItemIdsAreProvided() {
        List<Long> itemIds = List.of(1L, 2L);
        List<Booking> bookings = List.of(new Booking(), new Booking());
        when(bookingStorage.findAllByItemIdIn(itemIds)).thenReturn(bookings);

        List<Booking> result = bookingService.findAllByItemIdIn(itemIds);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bookingStorage, times(1)).findAllByItemIdIn(itemIds);
    }
    @Test
    void findAllByItemId_ShouldReturnListOfBookings_WhenItemIdIsProvided() {
        Long itemId = 1L;
        List<Booking> bookings = List.of(new Booking(), new Booking());
        when(bookingStorage.findAllByItemIdList(itemId)).thenReturn(bookings);

        List<Booking> result = bookingService.findAllByItemId(itemId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bookingStorage, times(1)).findAllByItemIdList(itemId);
    }
    @Test
    void findAllByItemIdAndBookerId_ShouldReturnBookings_WhenItemIdAndBookerIdAreProvided() {
        Long itemId = 1L;
        Long bookerId = 1L;
        List<Booking> expectedBookings = List.of(new Booking(), new Booking());
        when(bookingStorage.findAllByItemIdAndBookerId(itemId, bookerId)).thenReturn(expectedBookings);

        List<Booking> actualBookings = bookingService.findAllByItemIdAndBookerId(itemId, bookerId);

        assertNotNull(actualBookings);
        assertEquals(expectedBookings.size(), actualBookings.size());
        verify(bookingStorage, times(1)).findAllByItemIdAndBookerId(itemId, bookerId);
    }
    @Test
    void findCurrentBookingsByOwnerId_ShouldReturnCurrentBookings_WhenOwnerIdIsProvided() {
        Long ownerId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = Pageable.unpaged();

        Booking currentBooking = new Booking();
        currentBooking.setStart(now.minusDays(1));
        currentBooking.setEnd(now.plusDays(1));

        List<Booking> expectedBookings = List.of(currentBooking);
        when(bookingStorage.findCurrentBookingsByOwnerId(eq(ownerId), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(expectedBookings);

        Iterable<Booking> actualBookings = bookingService.findCurrentBookingsByOwnerId(ownerId, now, now, pageable);

        assertNotNull(actualBookings);
        assertTrue(actualBookings.iterator().hasNext());
        Booking actualBooking = actualBookings.iterator().next();
        assertEquals(currentBooking.getStart(), actualBooking.getStart());
        assertEquals(currentBooking.getEnd(), actualBooking.getEnd());
        verify(bookingStorage, times(1)).findCurrentBookingsByOwnerId(eq(ownerId), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable));
    }
    @Test
    void findAllByItemOwnerId_ShouldReturnAllBookings_WhenOwnerIdIsProvided() {
        Long ownerId = 1L;
        Pageable pageable = Pageable.unpaged();
        List<Booking> expectedBookings = List.of(new Booking(), new Booking());

        when(bookingStorage.findAllByItemOwnerId(ownerId, pageable)).thenReturn(expectedBookings);

        Iterable<Booking> actualBookings = bookingService.findAllByItemOwnerId(ownerId, pageable);

        assertNotNull(actualBookings);
        assertEquals(expectedBookings.size(), ((List<Booking>) actualBookings).size());
        verify(bookingStorage, times(1)).findAllByItemOwnerId(ownerId, pageable);
    }
    @Test
    void findPastBookingsByOwnerId_ShouldReturnPastBookings_WhenOwnerIdIsProvided() {
        Long ownerId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = Pageable.unpaged();

        Booking pastBooking = new Booking();
        pastBooking.setStart(now.minusDays(10));
        pastBooking.setEnd(now.minusDays(5));

        List<Booking> expectedBookings = List.of(pastBooking);
        when(bookingStorage.findPastBookingsByOwnerId(ownerId, now, pageable)).thenReturn(expectedBookings);

        Iterable<Booking> actualBookings = bookingService.findPastBookingsByOwnerId(ownerId, now, pageable);

        assertNotNull(actualBookings);
        assertTrue(actualBookings.iterator().hasNext());
        Booking actualBooking = actualBookings.iterator().next();
        assertEquals(pastBooking.getStart(), actualBooking.getStart());
        assertEquals(pastBooking.getEnd(), actualBooking.getEnd());
        verify(bookingStorage, times(1)).findPastBookingsByOwnerId(ownerId, now, pageable);
    }
    @Test
    void findFutureBookingsByOwnerId_ShouldReturnFutureBookings_WhenOwnerIdIsProvided() {
        Long ownerId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = Pageable.unpaged();

        Booking futureBooking = new Booking();
        futureBooking.setStart(now.plusDays(5));
        futureBooking.setEnd(now.plusDays(10));

        List<Booking> expectedBookings = List.of(futureBooking);
        when(bookingStorage.findFutureBookingsByOwnerId(ownerId, now, pageable)).thenReturn(expectedBookings);

        Iterable<Booking> actualBookings = bookingService.findFutureBookingsByOwnerId(ownerId, now, pageable);

        assertNotNull(actualBookings);
        assertTrue(actualBookings.iterator().hasNext());
        Booking actualBooking = actualBookings.iterator().next();
        assertEquals(futureBooking.getStart(), actualBooking.getStart());
        assertEquals(futureBooking.getEnd(), actualBooking.getEnd());
        verify(bookingStorage, times(1)).findFutureBookingsByOwnerId(ownerId, now, pageable);
    }
    @Test
    void findBookingsByOwnerIdAndStatus_ShouldReturnBookings_WhenOwnerIdAndStatusAreProvided() {
        Long ownerId = 1L;
        BookingStatus status = BookingStatus.APPROVED;
        Pageable pageable = Pageable.unpaged();

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.APPROVED);

        List<Booking> expectedBookings = List.of(booking);
        when(bookingStorage.findBookingsByOwnerIdAndStatus(ownerId, status, pageable)).thenReturn(expectedBookings);

        Iterable<Booking> actualBookings = bookingService.findBookingsByOwnerIdAndStatus(ownerId, status, pageable);

        assertNotNull(actualBookings);
        assertTrue(actualBookings.iterator().hasNext());
        assertEquals(status, actualBookings.iterator().next().getStatus());
        verify(bookingStorage, times(1)).findBookingsByOwnerIdAndStatus(ownerId, status, pageable);
    }
    @Test
    void findAllByBookerId_ShouldReturnAllBookings_WhenBookerIdIsProvided() {
        Long bookerId = 1L;
        Pageable pageable = Pageable.unpaged();

        List<Booking> expectedBookings = List.of(new Booking(), new Booking());
        when(bookingStorage.findAllByBookerId(bookerId, pageable)).thenReturn(expectedBookings);

        Iterable<Booking> actualBookings = bookingService.findAllByBookerId(bookerId, pageable);

        assertNotNull(actualBookings);
        assertEquals(expectedBookings.size(), ((List<Booking>) actualBookings).size());
        verify(bookingStorage, times(1)).findAllByBookerId(bookerId, pageable);
    }
    @Test
    void findCurrentBookingsByBookerId_ShouldReturnCurrentBookings_WhenBookerIdIsProvided() {
        Long bookerId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = Pageable.unpaged();

        Booking currentBooking = new Booking();
        currentBooking.setStart(now.minusDays(1));
        currentBooking.setEnd(now.plusDays(1));

        List<Booking> expectedBookings = List.of(currentBooking);
        when(bookingStorage.findCurrentBookingsByBookerId(bookerId, now, now, pageable)).thenReturn(expectedBookings);

        Iterable<Booking> actualBookings = bookingService.findCurrentBookingsByBookerId(bookerId, now, now, pageable);

        assertNotNull(actualBookings);
        assertTrue(actualBookings.iterator().hasNext());
        Booking actualBooking = actualBookings.iterator().next();
        assertEquals(currentBooking.getStart(), actualBooking.getStart());
        assertEquals(currentBooking.getEnd(), actualBooking.getEnd());
        verify(bookingStorage, times(1)).findCurrentBookingsByBookerId(bookerId, now, now, pageable);
    }
    @Test
    void findPastBookingsByBookerId_ShouldReturnPastBookings_WhenBookerIdIsProvided() {
        Long bookerId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = Pageable.unpaged();

        Booking pastBooking = new Booking();
        pastBooking.setStart(now.minusDays(10));
        pastBooking.setEnd(now.minusDays(5));

        List<Booking> expectedBookings = List.of(pastBooking);
        when(bookingStorage.findPastBookingsByBookerId(bookerId, now, pageable)).thenReturn(expectedBookings);

        Iterable<Booking> actualBookings = bookingService.findPastBookingsByBookerId(bookerId, now, pageable);

        assertNotNull(actualBookings);
        assertTrue(actualBookings.iterator().hasNext());
        Booking actualBooking = actualBookings.iterator().next();
        assertEquals(pastBooking.getStart(), actualBooking.getStart());
        assertEquals(pastBooking.getEnd(), actualBooking.getEnd());
        verify(bookingStorage, times(1)).findPastBookingsByBookerId(bookerId, now, pageable);
    }
    @Test
    void findFutureBookingsByBookerId_ShouldReturnFutureBookings_WhenBookerIdIsProvided() {
        Long bookerId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = Pageable.unpaged();

        Booking futureBooking = new Booking();
        futureBooking.setStart(now.plusDays(5));
        futureBooking.setEnd(now.plusDays(10));

        List<Booking> expectedBookings = List.of(futureBooking);
        when(bookingStorage.findFutureBookingsByBookerId(bookerId, now, pageable)).thenReturn(expectedBookings);

        Iterable<Booking> actualBookings = bookingService.findFutureBookingsByBookerId(bookerId, now, pageable);

        assertNotNull(actualBookings);
        assertTrue(actualBookings.iterator().hasNext());
        Booking actualBooking = actualBookings.iterator().next();
        assertEquals(futureBooking.getStart(), actualBooking.getStart());
        assertEquals(futureBooking.getEnd(), actualBooking.getEnd());
        verify(bookingStorage, times(1)).findFutureBookingsByBookerId(bookerId, now, pageable);
    }
    @Test
    void findBookingsByBookerIdAndStatus_ShouldReturnBookings_WhenBookerIdAndStatusAreProvided() {
        Long bookerId = 1L;
        BookingStatus status = BookingStatus.APPROVED;
        Pageable pageable = Pageable.unpaged();

        Booking bookingWithStatus = new Booking();
        bookingWithStatus.setStatus(BookingStatus.APPROVED);

        List<Booking> expectedBookings = List.of(bookingWithStatus);
        when(bookingStorage.findBookingsByBookerIdAndStatus(bookerId, status, pageable)).thenReturn(expectedBookings);

        Iterable<Booking> actualBookings = bookingService.findBookingsByBookerIdAndStatus(bookerId, status, pageable);

        assertNotNull(actualBookings);
        assertTrue(actualBookings.iterator().hasNext());
        assertEquals(bookingWithStatus.getStatus(), actualBookings.iterator().next().getStatus());
        verify(bookingStorage, times(1)).findBookingsByBookerIdAndStatus(bookerId, status, pageable);
    }


}