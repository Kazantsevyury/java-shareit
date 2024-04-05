package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemStorage itemStorage;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemServiceImpl itemService;

    private ItemDto itemDto;
    private UserDto userDto;
    private Item item;

    @BeforeEach
    public void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item1");
        itemDto.setDescription("Description1");
        itemDto.setAvailable(true);

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("User1");
        userDto.setEmail("user1@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Item1");
        item.setDescription("Description1");
        item.setAvailable(true);
    }

    @Test
    public void testAddItemWhenNonExistentUserIdThenThrowException() {
        when(userService.findUserById(1L)).thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class, () -> itemService.addItem(1L, itemDto));
    }

    @Test
    public void testAddItemWhenNullItemDtoThenThrowException() {
        when(userService.findUserById(1L)).thenReturn(userDto);

        assertThrows(NullPointerException.class, () -> itemService.addItem(1L, null));
    }
}