package ru.practicum.shareit.Item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@SpringBootTest
public class ItemServiceUnitTest {

    @MockBean
    private ItemStorage itemStorage;

    @MockBean
    private UserService userService;
    @MockBean
    private UserMapper userMapper;

    @Autowired
    private ItemServiceImpl itemService;

    @Test
    public void testAddItem() {
        UserDto userDto = new UserDto(1L, "Odin", "odin@yandex.ru");
        when(userService.findUserById(1L)).thenReturn(userDto);

        ItemDto itemDto = new ItemDto(null, "Skidbladnir", "Ship of Freyr", true, null);
        Item item = new Item(1L, "Skidbladnir", "Ship of Freyr", true, userMapper.fromUserDto(userDto), null);

        when(itemStorage.save(any(Item.class))).thenReturn(item);

        ItemDto savedItem = itemService.addItem(1L, itemDto);

        assertEquals("Skidbladnir", savedItem.getName());
        assertEquals("Ship of Freyr", savedItem.getDescription());
        assertTrue(savedItem.getAvailable());
    }
}
