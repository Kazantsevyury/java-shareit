package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Mapper(componentModel = "spring")
public interface BookingMapper {



    @Mapping(source = "booker.id", target = "bookerId")
    @Mapping(source = "item.id", target = "itemId")
    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "booker", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Booking fromAddBookingDto(AddBookingDto dto);


    @Mapping(target = "booker", source = "booker")
    @Mapping(target = "item", source = "item")
    BookingResponseDto toBookingResponseDto(Booking booking);
}
