package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingDto bookingToBookingDto(Booking booking);

    Booking bookingDtoToBooking(BookingDto bookingDto);
}
