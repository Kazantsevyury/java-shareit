package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@PathVariable long bookingId, @RequestBody BookingDto bookingDto) {
        bookingDto.setId(bookingId);
        return bookingService.updateBooking(bookingDto);
    }

    @GetMapping
    public Collection<BookingDto> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    @DeleteMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable long bookingId) {
        bookingService.removeBooking(bookingId);
    }
}
