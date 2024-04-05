package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ItemServiceImplTestUpdateItem {

    @Mock
    private ItemStorage itemStorage;
    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Long validUserId;
    private Long invalidUserId;
    private Long itemId;
    private Item item;
    private ItemUpdateDto itemUpdateDto;
    private User owner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        validUserId = 1L;
        invalidUserId = 2L;
        itemId = 100L;
        owner = new User();
        owner.setId(validUserId);

        item = new Item();
        item.setId(itemId);
        item.setOwner(owner);
        item.setName("Original Name");
        item.setDescription("Original Description");
        item.setAvailable(true);

        itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName("Updated Name");
        itemUpdateDto.setDescription("Updated Description");
        itemUpdateDto.setAvailable(false);

        ItemDto mockedItemDto = new ItemDto();
        mockedItemDto.setName(itemUpdateDto.getName());
        mockedItemDto.setDescription(itemUpdateDto.getDescription());
        mockedItemDto.setAvailable(itemUpdateDto.getAvailable());

        when(itemMapper.toDto(any(Item.class))).thenReturn(mockedItemDto);
    }

    @Test
    void updateItem_Success() {
        // Assuming this setup exists somewhere in your test class
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        when(itemStorage.save(any(Item.class))).thenReturn(item); // Make sure this behavior is defined

        // The operation under test
        ItemDto result = itemService.updateItem(validUserId, itemId, itemUpdateDto);

        // The failing assertion
        assertNotNull(result, "The result should not be null");

        // Additional assertions to verify the update was successful
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    void updateItem_ItemNotFound() {
        when(itemStorage.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
                () -> itemService.updateItem(validUserId, itemId, itemUpdateDto));

        assertEquals("Item с ID 100 не найден", exception.getMessage());
    }

    @Test
    void updateItem_UserNotOwner() {
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));

        Exception exception = assertThrows(RuntimeException.class,
                () -> itemService.updateItem(invalidUserId, itemId, itemUpdateDto));

        assertEquals("User не является хозяином. ", exception.getMessage());
    }

}
