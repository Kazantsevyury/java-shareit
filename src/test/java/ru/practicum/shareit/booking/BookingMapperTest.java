package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookingMapperTest {

    private BookingMapper bookingMapper;

    @BeforeEach
    void setUp() {
        bookingMapper = Mappers.getMapper(BookingMapper.class);
    }

    @Test
    public void testToDtoWhenValidBookingThenReturnBookingDto() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        BookingDto bookingDto = bookingMapper.toDto(booking);

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getId()).isEqualTo(booking.getId());
        assertThat(bookingDto.getStatus()).isEqualTo(booking.getStatus());
        assertThat(bookingDto.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingDto.getEnd()).isEqualTo(booking.getEnd());
    }

    @Test
    public void testToDtoWhenNullBookingThenReturnNull() {
        Booking booking = null;

        BookingDto bookingDto = bookingMapper.toDto(booking);

        assertThat(bookingDto).isNull();
    }

    @Test
    void testToDtoListWhenBookingsNotEmptyThenReturnDtoList() {
        Booking booking1 = new Booking(1L, null, null, BookingStatus.APPROVED, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        Booking booking2 = new Booking(2L, null, null, BookingStatus.WAITING, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        List<BookingDto> bookingDtos = bookingMapper.toDtoList(bookings);

        assertNotNull(bookingDtos);
        assertEquals(bookings.size(), bookingDtos.size());
        for (int i = 0; i < bookings.size(); i++) {
            assertEquals(bookings.get(i).getId(), bookingDtos.get(i).getId());
            assertEquals(bookings.get(i).getStatus(), bookingDtos.get(i).getStatus());
            assertEquals(bookings.get(i).getStart(), bookingDtos.get(i).getStart());
            assertEquals(bookings.get(i).getEnd(), bookingDtos.get(i).getEnd());
        }
    }

    @Test
    void testToDtoListWhenBookingsEmptyThenReturnEmptyList() {
        List<Booking> bookings = Collections.emptyList();

        List<BookingDto> bookingDtos = bookingMapper.toDtoList(bookings);

        assertNotNull(bookingDtos);
        assertTrue(bookingDtos.isEmpty());
    }



    @Test
    public void testToShortDtoWhenBookingIsValidThenReturnShortBookingDto() {
        // Arrange
        User booker = new User();
        booker.setId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));

        // Act
        ShortBookingDto shortBookingDto = bookingMapper.toShortDto(booking);

        // Assert
        assertThat(shortBookingDto).isNotNull();
        assertThat(shortBookingDto.getId()).isEqualTo(booking.getId());
        assertThat(shortBookingDto.getBookerId()).isEqualTo(booker.getId());
        assertThat(shortBookingDto.getStatus()).isEqualTo(booking.getStatus());
        assertThat(shortBookingDto.getStart()).isEqualTo(booking.getStart());
        assertThat(shortBookingDto.getEnd()).isEqualTo(booking.getEnd());
    }

    @Test
    public void testToShortDtoWhenBookerIsNullThenReturnShortBookingDtoWithNullBookerId() {
        // Arrange
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(null);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));

        // Act
        ShortBookingDto shortBookingDto = bookingMapper.toShortDto(booking);

        // Assert
        assertThat(shortBookingDto).isNotNull();
        assertThat(shortBookingDto.getId()).isEqualTo(booking.getId());
        assertThat(shortBookingDto.getBookerId()).isNull();
        assertThat(shortBookingDto.getStatus()).isEqualTo(booking.getStatus());
        assertThat(shortBookingDto.getStart()).isEqualTo(booking.getStart());
        assertThat(shortBookingDto.getEnd()).isEqualTo(booking.getEnd());
    }
}