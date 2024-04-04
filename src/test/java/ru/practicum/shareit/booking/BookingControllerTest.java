package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.GetBookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.ItemBookingFacade;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private ItemBookingFacade itemBookingFacade;

    private final Long userId = 1L;
    private final String HEADER_NAME = "X-Sharer-User-Id";

    private AddBookingDto validBookingDto;
    private BookingDto responseBookingDto;

    @BeforeEach
    void setUp() {
        validBookingDto = AddBookingDto.builder()
                .itemId(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(5))
                .build();

        responseBookingDto = new BookingDto();
    }

    @Test
    void addBooking_WhenBookingStartIsInPast_ShouldReturnBadRequestStatus() throws Exception {
        AddBookingDto bookingWithPastStartDate = new AddBookingDto(validBookingDto.getItemId(),
                LocalDateTime.now().minusDays(1), validBookingDto.getEnd());

        mockMvc.perform(post("/bookings")
                        .header(HEADER_NAME, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingWithPastStartDate)))
                .andExpect(status().isBadRequest());

        verify(itemBookingFacade, never()).addBooking(eq(userId), any(AddBookingDto.class));
    }

    @Test
    void acknowledgeBooking_WithValidParams_ShouldReturnOkStatusAndBookingDto() throws Exception {
        Long bookingId = 2L;
        Boolean approved = true;
        when(itemBookingFacade.acknowledgeBooking(eq(userId), eq(bookingId), eq(approved))).thenReturn(responseBookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(HEADER_NAME, userId)
                        .param("approved", approved.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseBookingDto)));

        verify(itemBookingFacade).acknowledgeBooking(eq(userId), eq(bookingId), eq(approved));
    }

    @Test
    void getBookingById_WithValidUserIdAndBookingId_ShouldReturnOkStatusAndBookingDto() throws Exception {
        Long bookingId = 3L;
        when(itemBookingFacade.getBookingById(eq(userId), eq(bookingId))).thenReturn(responseBookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(HEADER_NAME, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseBookingDto)));

        verify(itemBookingFacade).getBookingById(eq(userId), eq(bookingId));
    }

    @Test
    void acknowledgeBooking_WithoutHeader_ShouldReturnBadRequestStatus() throws Exception {
        Long bookingId = 2L;
        Boolean approved = true;

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", approved.toString()))
                .andExpect(status().isBadRequest());

        verify(itemBookingFacade, never()).acknowledgeBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void acknowledgeBooking_InvalidApprovedParam_ShouldReturnBadRequestStatus() throws Exception {
        Long bookingId = 2L;
        String invalidApproved = "invalid";

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(HEADER_NAME, userId)
                        .param("approved", invalidApproved))
                .andExpect(status().isBadRequest());

        verify(itemBookingFacade, never()).acknowledgeBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getAllBookingsFromUser_WithValidParameters_ShouldReturnOkStatus() throws Exception {
        when(itemBookingFacade.getAllBookingsFromUser(eq(userId), any(GetBookingState.class), anyLong(), anyInt(), eq(false)))
                .thenReturn(List.of(responseBookingDto));

        mockMvc.perform(get("/bookings")
                        .header(HEADER_NAME, userId)
                        .param("state", GetBookingState.ALL.name())
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").exists());

        verify(itemBookingFacade).getAllBookingsFromUser(eq(userId), any(GetBookingState.class), anyLong(), anyInt(), eq(false));
    }

    @Test
    void getAllOwnerBookings_WithValidParameters_ShouldReturnOkStatus() throws Exception {
        when(itemBookingFacade.getAllBookingsFromUser(eq(userId), any(GetBookingState.class), anyLong(), anyInt(), eq(true)))
                .thenReturn(List.of(responseBookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER_NAME, userId)
                        .param("state", GetBookingState.ALL.name())
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").exists());

        verify(itemBookingFacade).getAllBookingsFromUser(eq(userId), any(GetBookingState.class), anyLong(), anyInt(), eq(true));
    }

    @Test
    void getAllOwnerBookings_WithoutHeader_ShouldReturnBadRequestStatus() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .param("state", GetBookingState.ALL.name())
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(itemBookingFacade, never()).getAllBookingsFromUser(anyLong(), any(GetBookingState.class), anyLong(), anyInt(), anyBoolean());
    }

    @Test
    void getAllOwnerBookings_UnknownState_ShouldReturnBadRequestStatus() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER_NAME, userId)
                        .param("state", "UNKNOWN_STATE")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(itemBookingFacade, never()).getAllBookingsFromUser(anyLong(), any(GetBookingState.class), anyLong(), anyInt(), anyBoolean());
    }
}