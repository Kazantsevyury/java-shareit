package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import lombok.Builder;
import ru.practicum.shareit.ValidateDateRange;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateDateRange(start = "start", end = "end", message = "Задан некорректный интервал бронирования.")
public class AddBookingDto {

    @NotNull(message = "Не указан идентификатор вещи.")
    private Long itemId;

    @NotNull(message = "Не указана дата начала бронирования.")
    private LocalDateTime start;

    @NotNull(message = "Не указана дата окончания бронирования.")
    private LocalDateTime end;
}
