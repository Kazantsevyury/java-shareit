package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MissingRequestHeaderException;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private AddItemRequestDto sagaRequestDto;
    private ItemRequestDto runeRequestDto;
    private String header;
    private Long userId;

    @BeforeEach
    public void setUp() {
        sagaRequestDto = new AddItemRequestDto("A quest for the legendary Mjolnir");
        runeRequestDto = new ItemRequestDto();
        header = "X-Sharer-User-Id";
        userId = 1L;
    }

    @Test
    @SneakyThrows
    public void addNewSagaRequest_WithoutHeader_ShouldThrowMissingRequestHeaderException() {
        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(sagaRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingRequestHeaderException));

        verify(itemRequestService, never()).addNewItemRequest(any(), any());
    }

    @Test
    @SneakyThrows
    void getAllSagaRequestsFromViking_NoHeader_ShouldThrowMissingRequestHeaderException() {
        mvc.perform(get("/requests")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingRequestHeaderException));

        verify(itemRequestService, never()).addNewItemRequest(any(), any());
    }

    @Test
    @SneakyThrows
    public void getAllSagaRequestsFromViking_Valid_ShouldReturnRequest() {
        when(itemRequestService.getAllItemRequestsFromUser(userId))
                .thenReturn(List.of(runeRequestDto));

        mvc.perform(get("/requests")
                        .header(header, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(runeRequestDto))));

        verify(itemRequestService, times(1)).getAllItemRequestsFromUser(userId);
    }

    @Test
    @SneakyThrows
    public void getAvailableSagaRequests_NoHeader_ShouldThrowMissingRequestHeaderException() {
        mvc.perform(get("/requests/all"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingRequestHeaderException));

        verify(itemRequestService, never()).getAvailableItemRequests(any(), any(), any());
    }

    @Test
    @SneakyThrows
    public void getSagaRequestById_WithoutHeader_ShouldThrowMissingRequestHeaderException() {
        mvc.perform(get("/requests/{requestId}", 1))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingRequestHeaderException));

        verify(itemRequestService, never()).getItemRequestById(any(), any());
    }

    @Test
    @SneakyThrows
    public void getSagaRequestById_WithRequestId_ShouldReturnRequest() {
        long requestId = 2;
        when(itemRequestService.getItemRequestById(userId, requestId))
                .thenReturn(runeRequestDto);

        mvc.perform(get("/requests/{requestId}", String.valueOf(requestId))
                        .header(header, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(runeRequestDto)));

        verify(itemRequestService, times(1)).getItemRequestById(userId, requestId);
    }
}