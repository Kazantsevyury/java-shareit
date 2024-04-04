package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.GetBookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exception.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exception.exceptions.ItemOwnershipException;
import ru.practicum.shareit.exception.exceptions.ItemUnavailableException;
import ru.practicum.shareit.item.ItemBookingFacade;


import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;
    @MockBean
    private ItemBookingFacade itemBookingFacade;
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private long userId;

    private String header;

    private AddBookingDto addBookingDto;

    private BookingDto bookingDto;


    @BeforeEach
    void setUp() {
        userId = 1;
        header = "X-Sharer-User-Id";
        addBookingDto = AddBookingDto.builder()
                .itemId(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(5))
                .build();
        bookingDto = new BookingDto();
    }

    @Test
    @SneakyThrows
    void addNewBooking_WithoutHeader_ShouldThrowMissingRequestHeaderException() {

        mvc.perform(post("/bookings")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addBookingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingRequestHeaderException));

        verify(itemBookingFacade, never()).addBooking(any(), any());
    }

    @Test
    @SneakyThrows
    void addNewBooking_WhenNotFoundThrown_ShouldReturn404Status() {
        when(itemBookingFacade.addBooking(userId, addBookingDto))
                .thenThrow(BookingNotFoundException.class);

        mvc.perform(post("/bookings")
                        .header(header, userId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addBookingDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookingNotFoundException));

        verify(itemBookingFacade, times(1)).addBooking(userId, addBookingDto);
    }

    @Test
    @SneakyThrows
    void addNewBooking_WhenNotAuthorizedThrown_ShouldReturn404() {
        when(itemBookingFacade.addBooking(userId, addBookingDto))
                .thenThrow(ItemOwnershipException.class);

        mvc.perform(post("/bookings")
                        .header(header, userId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addBookingDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ItemOwnershipException));

        verify(itemBookingFacade, times(1)).addBooking(userId, addBookingDto);
    }

    @Test
    @SneakyThrows
    void acknowledgeBooking_WithAllParams_ShouldReturnStatus200() {
        Long bookingId = 2L;
        Boolean approved = true;
        when(itemBookingFacade.acknowledgeBooking(userId, bookingId, approved))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(header, userId)
                        .param("approved", approved.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(bookingDto)));

        verify(itemBookingFacade, times(1)).acknowledgeBooking(userId, bookingId, approved);
    }

    @Test
    @SneakyThrows
    void acknowledgeBooking_WhenItemUnavailableExceptionThrown_ShouldReturn404() {
        Long bookingId = 2L;
        Boolean approved = true;
        when(itemBookingFacade.acknowledgeBooking(userId, bookingId, approved))
                .thenThrow(ItemUnavailableException.class);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(header, userId)
                        .param("approved", approved.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ItemUnavailableException));

        verify(itemBookingFacade, times(1)).acknowledgeBooking(userId, bookingId, approved);
    }


    @Test
    @SneakyThrows
    void acknowledgeBooking_WithoutHeader_ShouldThrowMissingRequestHeaderException() {
        Long bookingId = 2L;
        Boolean approved = true;
        when(itemBookingFacade.acknowledgeBooking(userId, bookingId, approved))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", approved.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingRequestHeaderException));

        verify(itemBookingFacade, never()).acknowledgeBooking(any(), any(), any());
    }

    @Test
    @SneakyThrows
    void acknowledgeBooking_WithoutApproved_ShouldThrowMissingServletRequestParameterException() {
        Long bookingId = 2L;
        Boolean approved = true;
        when(itemBookingFacade.acknowledgeBooking(userId, bookingId, approved))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(header, userId))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MissingServletRequestParameterException));

        verify(itemBookingFacade, never()).acknowledgeBooking(any(), any(), any());
    }

    @Test
    @SneakyThrows
    void getBookingById_WithAllParameters_ShouldReturnStatus200() {
        Long bookingId = 2L;
        when(itemBookingFacade.getBookingById(userId, bookingId))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(header, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(bookingDto)));

        verify(itemBookingFacade, times(1)).getBookingById(userId, bookingId);
    }

    @Test
    @SneakyThrows
    void getBookingById_WithoutHeader_ShouldThrowMissingRequestHeaderException() {
        Long bookingId = 2L;

        mvc.perform(get("/bookings/{bookingId}", bookingId))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingRequestHeaderException));

        verify(itemBookingFacade, never()).getBookingById(any(), any());
    }

    @Test
    @SneakyThrows
    void getAllBookingsFromUser_WithAllParams_ShouldReturnStatus200() {
        GetBookingState state = GetBookingState.FUTURE;
        Long from = 1L;
        Integer size = 5;
        boolean isOwner = false;
        when(itemBookingFacade.getAllBookingsFromUser(userId, state, from, size, isOwner))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header(header, userId)
                        .param("state", state.name())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(bookingDto))));

        verify(itemBookingFacade, times(1)).getAllBookingsFromUser(userId, state, from, size, isOwner);
    }

    @Test
    @SneakyThrows
    void getAllBookingsFromUser_WithoutParams_ShouldReturnStatus200() {
        long from = 0;
        int size = 10;
        GetBookingState state = GetBookingState.ALL;
        boolean isOwner = false;
        when(itemBookingFacade.getAllBookingsFromUser(userId, state, from, size, isOwner))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header(header, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(bookingDto))));

        verify(itemBookingFacade, times(1)).getAllBookingsFromUser(userId, state, from, size,
                isOwner);
    }

    @Test
    @SneakyThrows
    void getAllBookingsFromUser_WithParams_ShouldReturnStatus200() {
        long from = 2;
        int size = 5;
        GetBookingState state = GetBookingState.ALL;
        boolean isOwner = false;
        when(itemBookingFacade.getAllBookingsFromUser(userId, state, from, size, isOwner))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header(header, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(bookingDto))));

        verify(itemBookingFacade, times(1)).getAllBookingsFromUser(userId, state, from, size,
                isOwner);
    }

    @Test
    @SneakyThrows
    void getAllBookingsFromUser_WithoutHeader_ShouldThrowMissingRequestHeaderException() {
        GetBookingState state = GetBookingState.FUTURE;
        Long from = 1L;
        Integer size = 5;
        boolean isOwner = false;

        mvc.perform(get("/bookings")
                        .param("state", state.name())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingRequestHeaderException));

        verify(itemBookingFacade, never()).getAllBookingsFromUser(any(), any(), any(), any(), eq(isOwner));
    }

    @Test
    @SneakyThrows
    void getAllBookingsFromUser_UnknownState_ShouldThrowMethodArgumentTypeMismatchException() {
        Long from = 1L;
        Integer size = 5;
        boolean isOwner = false;

        mvc.perform(get("/bookings")
                        .header(header, userId)
                        .param("state", "TEST")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException));

        verify(itemBookingFacade, never()).getAllBookingsFromUser(any(), any(), any(), any(), eq(isOwner));
    }

    @Test
    @SneakyThrows
    void getAllOwnerBookings_WithAllParams_ShouldReturnStatus200() {
        GetBookingState state = GetBookingState.FUTURE;
        Long from = 1L;
        Integer size = 5;
        boolean isOwner = true;
        when(itemBookingFacade.getAllBookingsFromUser(userId, state, from, size, isOwner))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header(header, userId)
                        .param("state", state.name())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(bookingDto))));

        verify(itemBookingFacade, times(1)).getAllBookingsFromUser(userId, state, from, size, isOwner);
    }

    @Test
    @SneakyThrows
    void getAllOwnerBookings_WithoutParams_ShouldReturnStatus200() {
        Long from = 0L;
        Integer size = 10;
        GetBookingState state = GetBookingState.ALL;
        boolean isOwner = true;
        when(itemBookingFacade.getAllBookingsFromUser(userId, state, from, size, isOwner))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header(header, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(bookingDto))));

        verify(itemBookingFacade, times(1)).getAllBookingsFromUser(userId, state, from, size,
                isOwner);
    }

    @Test
    @SneakyThrows
    void getAllOwnerBookings_WithoutHeader_ShouldThrowMissingRequestHeaderException() {
        GetBookingState state = GetBookingState.FUTURE;
        Long from = 1L;
        Integer size = 5;
        boolean isOwner = true;

        mvc.perform(get("/bookings/owner")
                        .param("state", state.name())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingRequestHeaderException));

        verify(itemBookingFacade, never()).getAllBookingsFromUser(any(), any(), any(), any(), eq(isOwner));
    }

    @Test
    @SneakyThrows
    void getAllOwnerBookings_UnknownState_ShouldThrowMethodArgumentTypeMismatchException() {
        Long from = 1L;
        Integer size = 5;
        boolean isOwner = true;

        mvc.perform(get("/bookings/owner")
                        .header(header, userId)
                        .param("state", "TEST")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException));

        verify(itemBookingFacade, never()).getAllBookingsFromUser(any(), any(), any(), any(), eq(isOwner));
    }
}