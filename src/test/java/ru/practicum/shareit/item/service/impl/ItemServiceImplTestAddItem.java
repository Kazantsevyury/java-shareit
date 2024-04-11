package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ItemServiceImplTestAddItem {

    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserService userService;
    @Mock
    private ItemRequestService itemRequestService;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Long userId;
    private ItemDto itemDto;
    private UserDto userDto;
    private Item item;
    private Item savedItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализация переменных
        userId = 1L;
        itemDto = new ItemDto();
        userDto = new UserDto();
        userDto.setId(userId);
        item = new Item();
        savedItem = new Item();

        // Общие настройки моков
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(itemMapper.toModel(any(ItemDto.class))).thenReturn(item);
        when(itemStorage.save(any(Item.class))).thenReturn(savedItem);
        when(itemMapper.toDto(any(Item.class))).thenReturn(itemDto);
    }

    @Test
    void addItem_SuccessWithoutRequestId() {

        ItemDto result = itemService.addItem(userId, itemDto);

        assertNotNull(result);
        verify(userService, times(1)).findUserById(userId);
        verify(itemMapper, times(1)).toModel(any(ItemDto.class));
        verify(itemStorage, times(1)).save(any(Item.class));
        verify(itemMapper, times(1)).toDto(any(Item.class));
    }

    @Test
    void addItem_SuccessWithRequestId() {
        ItemRequest itemRequest = new ItemRequest();
        itemDto.setRequestId(100L);
        when(itemRequestService.getPureItemRequestById(anyLong())).thenReturn(itemRequest);

        ItemDto result = itemService.addItem(userId, itemDto);

        assertNotNull(result);
        verify(itemRequestService, times(1)).getPureItemRequestById(anyLong());
        // Проверка, что item связан с itemRequest
        assertEquals(item.getRequest(), itemRequest);
    }

    @Test
    void addItem_UserNotFound() {
        when(userService.findUserById(anyLong())).thenThrow(new RuntimeException("User not found"));

        Exception exception = assertThrows(RuntimeException.class, () -> itemService.addItem(userId, itemDto));

        assertEquals("User not found", exception.getMessage());
        verify(itemStorage, never()).save(any(Item.class));
    }

    @Test
    void addItem_RequestNotFound() {
        when(itemRequestService.getPureItemRequestById(anyLong())).thenThrow(new RuntimeException("Request not found"));
        itemDto.setRequestId(100L);

        Exception exception = assertThrows(RuntimeException.class, () -> itemService.addItem(userId, itemDto));

        assertEquals("Request not found", exception.getMessage());
        // Проверяем, что элемент не был сохранен, так как запрос на вещь не найден
        verify(itemStorage, never()).save(any(Item.class));
    }
}
