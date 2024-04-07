package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@WebMvcTest(ItemRequestController.class)
public class AddNewItemRequest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void shouldAddNewItemRequest() throws Exception {
        Long userId = 1L;
        AddItemRequestDto addItemRequestDto = new AddItemRequestDto("Need a new laptop");
        ItemRequestDto expectedResponse = ItemRequestDto.builder()
                .id(1L)
                .description("Need a new laptop")
                .build();

        when(itemRequestService.addNewItemRequest(eq(userId), any(AddItemRequestDto.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"Need a new laptop\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResponse.getId()))
                .andExpect(jsonPath("$.description").value(expectedResponse.getDescription()));

        verify(itemRequestService).addNewItemRequest(eq(userId), any(AddItemRequestDto.class));
    }
}