package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.BookingStatus;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;

    @FutureOrPresent(message = "Дата начала должна быть в будущем или настоящем")
    private LocalDateTime start;

    @FutureOrPresent(message = "Дата окончания должна быть в будущем или настоящем")
    private LocalDateTime end;

    private BookingStatus status;

    @NotNull(message = "ID предмета не может быть пустым")
    private Long itemId;

    @NotNull(message = "ID арендатора не может быть пустым")
    private Long bookerId;
}