package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTestRemoveFindAllItemsByUserId {

    @Mock
    private ItemStorage itemStorage;
    @Mock
    private CommentStorage commentStorage;
    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private UserDto userDto;
    private Item item;

    @BeforeEach
    public void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@test.com");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(userMapper.fromUserDto(userDto));
    }

    @Test
    public void testRemoveItemWhenValidItemIdThenItemIsRemoved() {
        Long itemId = 1L;

        doNothing().when(itemStorage).deleteById(itemId);

        itemService.removeItem(itemId);

        verify(itemStorage, times(1)).deleteById(itemId);
    }

    @Test
    public void testRemoveItemWhenItemExistsThenItemIsRemoved() {
        Long itemId = 1L;

        doNothing().when(itemStorage).deleteById(itemId);

        itemService.removeItem(itemId);

        verify(itemStorage, times(1)).deleteById(itemId);
    }

    @Test
    public void testRemoveItemWhenItemDoesNotExistThenExceptionIsThrown() {
        Long itemId = 1L;

        doThrow(EmptyResultDataAccessException.class).when(itemStorage).deleteById(itemId);

        assertThrows(EmptyResultDataAccessException.class, () -> itemService.removeItem(itemId));

        verify(itemStorage, times(1)).deleteById(itemId);
    }

    @Test
    public void testFindAllItemsByUserIdWhenUserDoesNotExistThenThrowException() {
        when(userService.findUserById(1L)).thenThrow(new RuntimeException("User not found"));

        assertThatThrownBy(() -> itemService.findAllItemsByUserId(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
        verify(userService, times(1)).findUserById(1L);
    }

    @Test
    public void testSearchItemsWhenTextIsNotBlankThenReturnItemsList() {
        String text = "test";
        List<Item> items = Arrays.asList(new Item(), new Item());
        when(itemStorage.searchAvailableItemsByText(anyString())).thenReturn(items);
        when(itemMapper.toDto(any(Item.class))).thenReturn(new ItemDto());

        List<ItemDto> result = itemService.searchItems(text);

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(items.size());
    }

    @Test
    public void testSearchItemsWhenNoMatchingItemsThenReturnEmptyList() {
        String text = "test";
        List<Item> items = new ArrayList<>();
        when(itemStorage.searchAvailableItemsByText(text)).thenReturn(items);

        List<ItemDto> result = itemService.searchItems(text);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testSearchItemsWhenMatchingItemsThenReturnMatchingItems() {
        String text = "test";
        List<Item> items = new ArrayList<>();
        items.add(new Item());
        items.add(new Item());
        when(itemStorage.searchAvailableItemsByText(text)).thenReturn(items);
        when(itemMapper.toDto(any(Item.class))).thenReturn(new ItemDto());

        List<ItemDto> result = itemService.searchItems(text);

        assertFalse(result.isEmpty());
        assertEquals(items.size(), result.size());
    }

    @Test
    public void testGetItemsWithBookingsAndCommentsWhenItemsEmptyThenReturnEmptyList() {
        List<Booking> bookings = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();

        List<GetItemDto> result = itemService.getItemsWithBookingsAndComments(new ArrayList<>(), bookings, comments);

        assertEquals(new ArrayList<>(), result);
    }

    @Test
    public void testGetItemsWithBookingsAndCommentsWhenAllListsEmptyThenReturnEmptyList() {
        List<GetItemDto> result = itemService.getItemsWithBookingsAndComments(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        assertEquals(new ArrayList<>(), result);
    }

    @Test
    public void testAddItemWhenValidUserIdAndItemDtoThenReturnItemDto() {
        Long userId = 1L;
        ItemDto itemDto = new ItemDto();
        UserDto userDto = new UserDto();
        Item item = new Item();
        Item savedItem = new Item();

        when(userService.findUserById(userId)).thenReturn(userDto);
        when(itemMapper.toModel(itemDto)).thenReturn(item);
        when(userMapper.fromUserDto(userDto)).thenReturn(item.getOwner());
        when(itemStorage.save(item)).thenReturn(savedItem);
        when(itemMapper.toDto(savedItem)).thenReturn(itemDto);

        ItemDto result = itemService.addItem(userId, itemDto);

        assertEquals(itemDto, result);
    }

    @Test
    public void testAddItemWhenFindUserByIdThrowsExceptionThenThrowException() {
        Long userId = 1L;
        ItemDto itemDto = new ItemDto();

        when(userService.findUserById(userId)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> itemService.addItem(userId, itemDto));
    }

    @Test
    public void testAddItemWhenSaveThrowsExceptionThenThrowException() {
        Long userId = 1L;
        ItemDto itemDto = new ItemDto();
        UserDto userDto = new UserDto();
        Item item = new Item();

        when(userService.findUserById(userId)).thenReturn(userDto);
        when(itemMapper.toModel(itemDto)).thenReturn(item);
        when(userMapper.fromUserDto(userDto)).thenReturn(item.getOwner());
        when(itemStorage.save(item)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> itemService.addItem(userId, itemDto));
    }

    // ... existing code ...

}