package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ItemRequestMapperTest {

    private ItemRequestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ItemRequestMapper.class);
    }

    @Test
    void testToModel() {
        AddItemRequestDto addItemRequestDto = new AddItemRequestDto();
        // Initialize addItemRequestDto with necessary properties

        ItemRequest itemRequest = mapper.toModel(addItemRequestDto);

        assertNotNull(itemRequest);
        // Add assertions to validate the conversion
    }

    @Test
    void testToDto() {
        ItemRequest itemRequest = new ItemRequest();
        // Initialize itemRequest with necessary properties

        ItemRequestDto itemRequestDto = mapper.toDto(itemRequest);

        assertNotNull(itemRequestDto);
        // Add assertions to validate the conversion
    }

    @Test
    void testToDtoList() {
        ItemRequest itemRequest = new ItemRequest();
        // Initialize itemRequest with necessary properties

        // Assuming you have a list of item requests
        // Here, for simplicity, I'm creating a list with a single item request
        // You should create a list with multiple item requests for better testing
        List<ItemRequest> requests = Collections.singletonList(itemRequest);

        List<ItemRequestDto> itemRequestDtos = mapper.toDtoList(requests);

        assertNotNull(itemRequestDtos);
        assertEquals(1, itemRequestDtos.size());
        // Add assertions to validate the conversion
    }
}

