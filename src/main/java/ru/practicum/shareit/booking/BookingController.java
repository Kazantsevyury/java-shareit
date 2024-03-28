package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.AddBookingDto;
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

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final ItemBookingFacade itemBookingFacade;

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
        return itemBookingFacade.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<?> getAllBookingsFromUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        if ("UNSUPPORTED_STATUS".equals(state)) {
            throw new CustomBadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        try {
            GetBookingState bookingState = GetBookingState.valueOf(state);
            return ResponseEntity.ok(itemBookingFacade.getAllBookingsFromUser(userId, bookingState));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Unknown state: " + state);
        }
    }

    @GetMapping("/owner")
    public ResponseEntity<?> getAllOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        if ("UNSUPPORTED_STATUS".equals(state)) {
            throw new CustomBadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        try {
            GetBookingState bookingState = GetBookingState.valueOf(state);
            return ResponseEntity.ok(itemBookingFacade.getAllOwnerBookings(userId, bookingState));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Unknown state: " + state);
        }
    }
}
