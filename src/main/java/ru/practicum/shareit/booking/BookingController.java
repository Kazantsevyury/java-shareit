package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.exceptions.CustomBadRequestException;
import ru.practicum.shareit.item.ItemBookingFacade;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.GetBookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final ItemBookingFacade itemBookingFacade;
    private static final String DEFAULT_PAGE_SIZE = "10";

    @PostMapping
    public BookingDto addNewBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody AddBookingDto bookingDto) {
        return itemBookingFacade.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto acknowledgeBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long bookingId,
                                         @RequestParam Boolean approved) {
        return itemBookingFacade.acknowledgeBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId) {

        bookingService.getBookingByIdAndUserId(bookingId, userId);
        return itemBookingFacade.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsFromUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "ALL") GetBookingState state,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) @Positive Integer size) {
        return itemBookingFacade.getAllBookingsFromUser(userId, state, from, size, false);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(defaultValue = "ALL") GetBookingState state,
                                                @RequestParam(defaultValue = "0") Long from,
                                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) Integer size) {
        return itemBookingFacade.getAllBookingsFromUser(userId, state, from, size, true);
    }
}
