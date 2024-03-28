package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddBookingDto {
    @FutureOrPresent(message = "Start date must be in the future or present")
    private LocalDateTime start;
    @FutureOrPresent(message = "End date must be in the future or present")
    private LocalDateTime end;
    private Long itemId;
}