package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.ItemBookingFacade;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemBookingFacade itemBookingFacade;

    @Autowired
    private ObjectMapper objectMapper;

    private AddBookingDto runeStoneBooking;
    private BookingDto sagaBooking;

    @BeforeEach
    void setUp() {
        runeStoneBooking = new AddBookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        sagaBooking = new BookingDto(1L, null, null, null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
    }

    @Test
    @SneakyThrows
    void bookRuneStone_Success() {
        when(itemBookingFacade.addBooking(eq(1L), any(AddBookingDto.class))).thenReturn(sagaBooking);

        mockMvc.perform(post("/bookings")
                        .header("X-Viking-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(runeStoneBooking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sagaBooking.getId()));

        verify(itemBookingFacade, times(1)).addBooking(eq(1L), any(AddBookingDto.class));
    }

    @Test
    @SneakyThrows
    void viewSagaById_Success() {
        when(itemBookingFacade.getBookingById(1L, 1L)).thenReturn(sagaBooking);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Viking-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sagaBooking.getId()));

        verify(itemBookingFacade, times(1)).getBookingById(1L, 1L);
    }

    @Test
    @SneakyThrows
    void confirmRuneStoneBooking_Success() {
        when(itemBookingFacade.acknowledgeBooking(eq(1L), eq(1L), eq(true))).thenReturn(sagaBooking);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Viking-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sagaBooking.getId()));

        verify(itemBookingFacade, times(1)).acknowledgeBooking(eq(1L), eq(1L), eq(true));
    }

    @Test
    @SneakyThrows
    void listVikingBookings_Success() {
        when(itemBookingFacade.getAllBookingsFromUser(eq(1L), any(), anyLong(), anyInt(), eq(false)))
                .thenReturn(Arrays.asList(sagaBooking));

        mockMvc.perform(get("/bookings")
                        .header("X-Viking-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sagaBooking.getId()));

        verify(itemBookingFacade, times(1)).getAllBookingsFromUser(eq(1L), any(), anyLong(), anyInt(), eq(false));
    }
}
