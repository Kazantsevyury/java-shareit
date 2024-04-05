package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.model.User;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemStorage itemStorage;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    private ArgumentCaptor<Item> itemCaptor;

    private Long userId;
    private ItemDto itemDto;
    private UserDto userDto;
    private Item item;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        itemDto = new ItemDto();
        userDto = new UserDto();
        item = new Item();
    }

    @Test
    public void testAddItemWhenValidUserIdAndItemDtoThenReturnItemDto() {
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(itemMapper.toModel(itemDto)).thenReturn(item);
        when(userMapper.fromUserDto(userDto)).thenReturn(new User());
        when(itemStorage.save(item)).thenReturn(item);
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.addItem(userId, itemDto);

        assertEquals(itemDto, result);
    }

    @Test
    public void testAddItemWhenInvalidUserIdThenThrowException() {
        when(userService.findUserById(userId)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> itemService.addItem(userId, itemDto));
    }

    @Test
    public void testAddItemWhenItemDtoHasRequestIdThenSetRequestAndReturnSavedItemDto() {
        Long requestId = 1L;
        itemDto.setRequestId(requestId);
        ItemRequest itemRequest = new ItemRequest();

        when(userService.findUserById(userId)).thenReturn(userDto);
        when(itemMapper.toModel(any(ItemDto.class))).thenReturn(item);
        when(userMapper.fromUserDto(any(UserDto.class))).thenReturn(new User());
        when(itemRequestService.getPureItemRequestById(requestId)).thenReturn(itemRequest);
        when(itemStorage.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toDto(any(Item.class))).thenReturn(itemDto);

        ItemDto result = itemService.addItem(userId, itemDto);

        assertEquals(itemDto, result);

        // Capture the item passed to save method
        verify(itemStorage).save(itemCaptor.capture());
        Item capturedItem = itemCaptor.getValue();

        // Assert that the item request was set correctly
        assertEquals(itemRequest, capturedItem.getRequest());
    }

    @Test
    public void testAddItemWhenFindUserByIdThrowsExceptionThenThrowException() {
        when(userService.findUserById(userId)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> itemService.addItem(userId, itemDto));
    }

    @Test
    public void testAddItemWhenSaveThrowsExceptionThenThrowException() {
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(itemMapper.toModel(itemDto)).thenReturn(item);
        when(userMapper.fromUserDto(userDto)).thenReturn(new User());
        when(itemStorage.save(item)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> itemService.addItem(userId, itemDto));
    }
}
