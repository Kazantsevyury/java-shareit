package ru.practicum.shareit.booking;
import org.springframework.context.annotation.Lazy;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.ItemBookingFacade;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    @Lazy
    private final BookingService bookingService;
    private final ItemBookingFacade itemBookingFacade;

    @PostMapping
    public ResponseEntity<BookingResponseDto> addNewBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestBody AddBookingDto bookingDto) {
        BookingResponseDto createdBooking = itemBookingFacade.createBooking(userId, bookingDto);
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto acknowledgeBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam Boolean approved) {
        return bookingService.acknowledgeBooking(userId, bookingId, approved);
    }


    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookingsFromUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getAllBookingsFromUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getAllOwnerBookings(userId, state);
    }
}