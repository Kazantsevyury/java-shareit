package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ExceptionFactory;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTestGetPure {

    @Mock
    private ItemStorage itemStorage;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    public void testGetPureItemByIdWhenItemExistsThenReturnItem() {
        // Arrange
        Long itemId = 1L;
        Item expectedItem = new Item();
        expectedItem.setId(itemId);
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(expectedItem));

        // Act
        Item actualItem = itemService.getPureItemById(itemId);

        // Assert
        assertEquals(expectedItem, actualItem);
        verify(itemStorage, times(1)).findById(itemId);
    }

    @Test
    public void testGetPureItemByIdWhenItemDoesNotExistThenThrowException() {
        // Arrange
        Long itemId = 1L;
        when(itemStorage.findById(itemId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ExceptionFactory.entityNotFound("Item", itemId).getClass(), () -> itemService.getPureItemById(itemId));
        verify(itemStorage, times(1)).findById(itemId);
    }
}