package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.exceptions.*;
import ru.practicum.shareit.item.ItemBookingFacade;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {BookingController.class, ItemController.class, ItemRequestController.class, UserController.class})
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    // MockBeans for services used in controllers
    @MockBean
    private BookingService bookingService;

    @MockBean
    private ItemBookingFacade itemBookingFacade;

    @MockBean
    private ItemService itemService;

    @MockBean
    private ItemRequestService itemRequestService;

    @MockBean
    private UserService userService;

    @Test
    void whenUserNotFound_thenRespondWith404() throws Exception {
        Long userId = 1L;
        given(userService.findUserById(userId)).willThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void whenAccessDenied_thenRespondWith403() throws Exception {
        given(bookingService.getBookingByIdAndUserId(1L, 1L)).willThrow(new AccessDeniedException("Access is denied"));

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access is denied"))
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void whenEntityNotFound_thenRespondWith404() throws Exception {
        given(itemService.findItemById(1L, 1L)).willThrow(new EntityNotFoundException("Item not found"));

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Item not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void whenEntityAlreadyExists_thenRespondWith409() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto("name", "email@test.com");
        given(userService.addUser(any(UserCreateDto.class)))
                .willThrow(new EntityAlreadyExistsException("User already exists"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userCreateDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("User already exists"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void whenNotAuthorizedException_thenRespondWith401() throws Exception {
        given(bookingService.getBookingByIdAndUserId(anyLong(), anyLong())).willThrow(new NotAuthorizedException("Not authorized"));

        mockMvc.perform(get("/bookings/{id}", 1L)
                        .header("X-Sharer-User-Id", 2L)) // Используем ID, который не соответствует владельцу или создателю бронирования
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Not authorized"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void whenItemOwnershipException_thenRespondWith404() throws Exception {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(); // Предположим, что это DTO для обновления Item
        given(itemService.updateItem(anyLong(), anyLong(), any(ItemUpdateDto.class))).willThrow(new ItemOwnershipException("Not the item owner"));

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 2L) // Идентификатор пользователя, не являющегося владельцем
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemUpdateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not the item owner"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void whenBadRequestException_thenRespondWith400() throws Exception {
        given(userService.updateUser(anyLong(), any(UserUpdateDto.class)))
                .willThrow(new CustomBadRequestException("Bad request"));

        mockMvc.perform(patch("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new UserUpdateDto())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void whenBookingOwnershipException_thenRespondWith404() throws Exception {
        given(bookingService.getBookingByIdAndUserId(anyLong(), anyLong()))
                .willThrow(new BookingOwnershipException("Not the booking owner"));

        mockMvc.perform(get("/bookings/{id}", 1L)
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not the booking owner"))
                .andExpect(jsonPath("$.status").value(404));
    }
}