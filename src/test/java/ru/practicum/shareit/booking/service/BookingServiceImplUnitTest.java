package ru.practicum.shareit.booking.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.exceptions.BookingNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class BookingServiceImplUnitTest {

    @Mock
    private BookingStorage bookingStorage;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void whenFindBookingCalledWithValidIdThenReturnBooking() {
        Long bookingId = 1L;
        Booking expectedBooking = new Booking();
        when(bookingStorage.findBookingById(bookingId)).thenReturn(java.util.Optional.of(expectedBooking));

        Booking actualBooking = bookingService.findBooking(bookingId);

        assertNotNull(actualBooking);
        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    void whenFindBookingCalledWithInvalidIdThenThrowException() {
        Long bookingId = 1L;
        when(bookingStorage.findBookingById(bookingId)).thenReturn(java.util.Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.findBooking(bookingId));
    }
    @Test
    void whenFindAllByItemIdInCalledWithValidIdsThenReturnBookings() {
        List<Long> itemIds = Arrays.asList(1L, 2L);
        List<Booking> expectedBookings = Arrays.asList(new Booking(), new Booking());
        when(bookingStorage.findAllByItemIdIn(itemIds)).thenReturn(expectedBookings);

        List<Booking> actualBookings = bookingService.findAllByItemIdIn(itemIds);

        assertNotNull(actualBookings);
        assertEquals(expectedBookings.size(), actualBookings.size());
    }
    @Test
    void whenPureSaveIsCalledWithBookingThenReturnSavedBooking() {
        // Arrange
        Booking bookingToSave = new Booking();
        bookingToSave.setId(1L); // Assuming some properties are set to simulate a real entity.
        when(bookingStorage.save(bookingToSave)).thenReturn(bookingToSave);

        // Act
        Booking savedBooking = bookingService.pureSave(bookingToSave);

        // Assert
        assertNotNull(savedBooking);
        assertEquals(bookingToSave.getId(), savedBooking.getId());
        verify(bookingStorage).save(bookingToSave); // Ensure save method was called with the correct object
    }
    @Test
    void whenFindAllByItemIdCalledWithValidIdThenReturnBookingsList() {
        // Подготовка
        Long itemId = 1L; // ID предмета, для которого ищем бронирования
        List<Booking> expectedBookings = Arrays.asList(new Booking(), new Booking());
        when(bookingStorage.findAllByItemIdList(itemId)).thenReturn(expectedBookings);

        // Действие
        List<Booking> actualBookings = bookingService.findAllByItemId(itemId);

        // Проверки
        assertNotNull(actualBookings, "Список бронирований не должен быть null");
        assertEquals(expectedBookings.size(), actualBookings.size(), "Размер возвращаемого списка бронирований должен совпадать с ожидаемым");
        assertEquals(expectedBookings, actualBookings, "Возвращаемый список бронирований должен совпадать с ожидаемым");
        verify(bookingStorage).findAllByItemIdList(itemId); // Удостоверимся, что метод был вызван с правильным ID
    }
    @Test
    void whenFindAllByItemIdAndBookerIdCalledWithValidIdsThenReturnBookingsList() {
        // Подготовка
        Long itemId = 1L;  // ID предмета
        Long bookerId = 1L;  // ID пользователя, который сделал бронирование
        List<Booking> expectedBookings = Arrays.asList(new Booking(), new Booking());
        when(bookingStorage.findAllByItemIdAndBookerId(itemId, bookerId)).thenReturn(expectedBookings);

        // Действие
        List<Booking> actualBookings = bookingService.findAllByItemIdAndBookerId(itemId, bookerId);

        // Проверки
        assertNotNull(actualBookings, "Список бронирований не должен быть null");
        assertEquals(expectedBookings.size(), actualBookings.size(), "Размер возвращаемого списка бронирований должен совпадать с ожидаемым");
        assertEquals(expectedBookings, actualBookings, "Возвращаемый список бронирований должен совпадать с ожидаемым");
        verify(bookingStorage).findAllByItemIdAndBookerId(itemId, bookerId);  // Проверяем, что метод вызван с правильными параметрами
    }
    @Test
    void whenFindPastBookingsByBookerIdCalledWithValidIdThenReturnPastBookingsList() {
        // Подготовка
        Long bookerId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> expectedBookings = Arrays.asList(new Booking(), new Booking());
        when(bookingStorage.findPastBookingsByBookerId(bookerId, now, pageable)).thenReturn(expectedBookings);

        // Действие
        Iterable<Booking> actualBookings = bookingService.findPastBookingsByBookerId(bookerId, now, pageable);

        // Проверки
        assertNotNull(actualBookings, "Список прошлых бронирований не должен быть null");
        assertTrue(((Collection<?>) actualBookings).size() == expectedBookings.size(), "Размер возвращаемого списка прошлых бронирований должен совпадать с ожидаемым");
        verify(bookingStorage).findPastBookingsByBookerId(bookerId, now, pageable); // Проверяем вызов с правильными параметрами
    }
    @Test
    void whenFindCurrentBookingsByBookerIdCalledWithValidIdThenReturnCurrentBookingsList() {
        // Подготовка
        Long bookerId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> expectedBookings = Arrays.asList(new Booking(), new Booking());
        when(bookingStorage.findCurrentBookingsByBookerId(bookerId, now, now, pageable)).thenReturn(expectedBookings);

        // Действие
        Iterable<Booking> actualBookings = bookingService.findCurrentBookingsByBookerId(bookerId, now, now, pageable);

        // Проверки
        assertNotNull(actualBookings, "Список текущих бронирований не должен быть null");
        assertTrue(((Collection<?>) actualBookings).size() == expectedBookings.size(), "Размер возвращаемого списка текущих бронирований должен совпадать с ожидаемым");
        verify(bookingStorage).findCurrentBookingsByBookerId(bookerId, now, now, pageable); // Проверяем вызов с правильными параметрами
    }
    @Test
    void whenFindFutureBookingsByBookerIdCalledWithValidIdThenReturnFutureBookingsList() {
        // Подготовка
        Long bookerId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> expectedBookings = Arrays.asList(new Booking(), new Booking());
        when(bookingStorage.findFutureBookingsByBookerId(bookerId, now, pageable)).thenReturn(expectedBookings);

        // Действие
        Iterable<Booking> actualBookings = bookingService.findFutureBookingsByBookerId(bookerId, now, pageable);

        // Проверки
        assertNotNull(actualBookings, "Список будущих бронирований не должен быть null");
        assertTrue(((Collection<?>) actualBookings).size() == expectedBookings.size(), "Размер возвращаемого списка будущих бронирований должен совпадать с ожидаемым");
        verify(bookingStorage).findFutureBookingsByBookerId(bookerId, now, pageable); // Проверяем вызов с правильными параметрами
    }
    @Test
    void whenFindBookingsByBookerIdAndStatusCalledWithValidParamsThenReturnFilteredBookingsList() {
        // Подготовка
        Long bookerId = 1L;
        BookingStatus bookingStatus = BookingStatus.APPROVED;
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> expectedBookings = Arrays.asList(new Booking(), new Booking());
        when(bookingStorage.findBookingsByBookerIdAndStatus(bookerId, bookingStatus, pageable)).thenReturn(expectedBookings);

        // Действие
        Iterable<Booking> actualBookings = bookingService.findBookingsByBookerIdAndStatus(bookerId, bookingStatus, pageable);

        // Проверки
        assertNotNull(actualBookings, "Список бронирований по статусу не должен быть null");
        assertTrue(((Collection<?>) actualBookings).size() == expectedBookings.size(), "Размер возвращаемого списка бронирований должен совпадать с ожидаемым");
        verify(bookingStorage).findBookingsByBookerIdAndStatus(bookerId, bookingStatus, pageable); // Проверяем вызов с правильными параметрами
    }


}
