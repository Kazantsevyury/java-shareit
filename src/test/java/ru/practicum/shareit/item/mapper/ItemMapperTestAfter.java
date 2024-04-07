package ru.practicum.shareit.item.mapper;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ItemMapperTestAfter {
    @InjectMocks
    private ItemMapper itemMapper = new ItemMapperImpl();

    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        itemRequest = new ItemRequest();
        itemRequest.setId(1L);

        item = new Item();
        item.setId(2L);
        item.setName("Drill");
        item.setDescription("A powerful drill");
        item.setAvailable(true);
        item.setRequest(itemRequest);
    }

    @Test
    void whenItemHasRequest_thenRequestIdShouldBeMapped() {
        ItemDto itemDto = itemMapper.toDto(item);

        assertThat(itemDto.getRequestId()).isEqualTo(item.getRequest().getId());
    }

    @Test
    void whenItemHasNoRequest_thenRequestIdShouldBeNull() {
        item.setRequest(null); // No request associated
        ItemDto itemDto = itemMapper.toDto(item);

        assertThat(itemDto.getRequestId()).isNull();
    }
}

