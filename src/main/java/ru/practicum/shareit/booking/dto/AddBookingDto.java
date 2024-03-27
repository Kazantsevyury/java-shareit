package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddBookingDto {

    @FutureOrPresent(message = "Start date must be in the future or present")
    private LocalDateTime startDate;
    @FutureOrPresent(message = "End date must be in the future or present")
    private LocalDateTime endDate;
    @NotNull(message = "Item ID cannot be null")
    private Long itemId;
}