package ru.practicum.shareit.Item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemBookingFacade;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private ItemBookingFacade itemBookingFacade;

    private GetItemDto getItemDto;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        getItemDto = GetItemDto.builder()
                .id(1L)
                .name("Mjolnir")
                .description("Hammer of Thor")
                .available(true)
                .build();
        itemDto = new ItemDto(1L, "Mjolnir", "Hammer of Thor", true, null);
    }

    @Test
    void addItem_ShouldReturnNewItem() throws Exception {
        when(itemBookingFacade.addItem(anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content("{\"name\":\"Mjolnir\",\"description\":\"Hammer of Thor\",\"available\":true}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Gungnir", "Spear of Odin", true, null);
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemUpdateDto.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .content("{\"name\":\"Gungnir\",\"description\":\"Spear of Odin\",\"available\":true}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Gungnir")))
                .andExpect(jsonPath("$.description", is("Spear of Odin")));
    }

    @Test
    void getItemById_ShouldReturnItem() throws Exception {
        when(itemService.findItemById(anyLong(), anyLong())).thenReturn(getItemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(getItemDto.getName())))
                .andExpect(jsonPath("$.description", is(getItemDto.getDescription())));
    }

    @Test
    void getAllItemsByOwner_ShouldReturnItemList() throws Exception {
        List<GetItemDto> items = Arrays.asList(getItemDto);
        when(itemService.findAllItemsByUserId(anyLong())).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(getItemDto.getName())))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchItems_ShouldReturnFilteredItems() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Mjolnir", "Hammer of Thor", true, null);
        when(itemService.searchItems(any(String.class))).thenReturn(Arrays.asList(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Mjolnir"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Mjolnir")))
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
