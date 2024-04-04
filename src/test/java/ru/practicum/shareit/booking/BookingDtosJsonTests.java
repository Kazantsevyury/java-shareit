package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingDtosJsonTests {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void addBookingDto_SerializationDeserialization_Test() throws Exception {
        AddBookingDto dto = AddBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        String json = objectMapper.writeValueAsString(dto);
        AddBookingDto resultDto = objectMapper.readValue(json, AddBookingDto.class);

        assertThat(resultDto).usingRecursiveComparison().isEqualTo(dto);
    }

    @Test
    void bookingDto_SerializationDeserialization_Test() throws Exception {
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        String json = objectMapper.writeValueAsString(dto);
        BookingDto resultDto = objectMapper.readValue(json, BookingDto.class);

        assertThat(resultDto).usingRecursiveComparison().isEqualTo(dto);
    }

    @Test
    void shortBookingDto_SerializationDeserialization_Test() throws Exception {
        ShortBookingDto dto = new ShortBookingDto(
                1L,
                2L,
                BookingStatus.WAITING,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1));

        String json = objectMapper.writeValueAsString(dto);
        ShortBookingDto resultDto = objectMapper.readValue(json, ShortBookingDto.class);

        assertThat(resultDto).usingRecursiveComparison().isEqualTo(dto);
    }

}