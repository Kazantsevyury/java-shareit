package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.exception.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;

import java.util.Optional;

class ItemServiceImplUnitTest {

    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserService userService;
    @Mock
    private CommentStorage commentStorage;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private BookingServiceImpl bookingService;
    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addItemShouldCreateNewItem() {
        // Подготовка данных
        Long userId = 1L;
        ItemDto itemDto = new ItemDto(null, "Drill", "Powerful tool", true, null);
        UserDto userDto = new UserDto(userId, "John Doe", "john@example.com");
        Item item = new Item(1L, "Drill", "Powerful tool", true, null, null);
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(userMapper.fromUserDto(userDto)).thenReturn(new User(userId, "John Doe", "john@example.com"));
        when(itemMapper.toModel(itemDto)).thenReturn(item);
        when(itemStorage.save(item)).thenReturn(item);
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        // Вызов метода
        ItemDto result = itemService.addItem(userId, itemDto);

        // Проверки
        assertNotNull(result);
        assertEquals("Drill", result.getName());
        verify(itemStorage).save(any(Item.class));
        verify(userService).findUserById(userId);
    }

    @Test
    void addItemWithRequestShouldLinkToRequest() {
        // Подготовка данных
        Long userId = 1L;
        Long requestId = 2L;
        ItemDto itemDto = new ItemDto(null, "Bike", "Mountain bike", true, requestId);
        UserDto userDto = new UserDto(userId, "Jane Doe", "jane@example.com");
        User user = User.builder().id(userId).name("Jane Doe").email("jane@example.com").build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(requestId)
                .description("Need a bike")
                .requester(user)
                .build();

        Item item = Item.builder()
                .name("Bike")
                .description("Mountain bike")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();

        when(userService.findUserById(userId)).thenReturn(userDto);
        when(userMapper.fromUserDto(userDto)).thenReturn(user);
        when(itemRequestService.getPureItemRequestById(requestId)).thenReturn(itemRequest);
        when(itemMapper.toModel(itemDto)).thenReturn(item);
        when(itemStorage.save(item)).thenReturn(item);
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        // Вызов метода
        ItemDto result = itemService.addItem(userId, itemDto);

        // Проверки
        assertNotNull(result);
        assertEquals(requestId, result.getRequestId());
        verify(itemRequestService).getPureItemRequestById(requestId);
    }

    @Test
    void updateItemShouldThrowAccessDeniedIfUserIsNotOwner() {
        // Подготовка данных
        Long userId = 1L; // ID пользователя, пытающегося обновить предмет
        Long ownerId = 2L; // ID владельца предмета
        Long itemId = 1L;
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("New Drill", "Updated powerful tool", true);

        User owner = User.builder().id(ownerId).name("John Doe").email("john@example.com").build();
        Item item = Item.builder()
                .id(itemId)
                .name("Drill")
                .description("Powerful tool")
                .available(true)
                .owner(owner)
                .build();

        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));

        // Вызов метода и проверка исключения
        Exception exception = assertThrows(RuntimeException.class,
                () -> itemService.updateItem(userId, itemId, itemUpdateDto),
                "Expected updateItem to throw, but it didn't");

        assertTrue(exception.getMessage().contains("User не является хозяином"));
        verify(itemStorage).findById(itemId); // Проверяем, был ли вызван метод findById
        verifyNoMoreInteractions(itemStorage); // Убеждаемся, что больше не было взаимодействий с хранилищем
    }

    @Test
    void updateItemShouldUpdateNameIfProvided() {
        // Подготовка данных
        Long userId = 1L;
        Long itemId = 1L;
        String originalName = "Drill";
        String updatedName = "New Drill";
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(updatedName, null, null);

        User owner = User.builder().id(userId).name("John Doe").email("john@example.com").build();
        Item item = Item.builder()
                .id(itemId)
                .name(originalName)
                .description("Powerful tool")
                .available(true)
                .owner(owner)
                .build();

        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        when(itemStorage.save(item)).thenReturn(item);
        ItemDto returnedDto = new ItemDto(itemId, updatedName, "Powerful tool", true, null);
        when(itemMapper.toDto(item)).thenReturn(returnedDto);

        // Вызов метода
        ItemDto result = itemService.updateItem(userId, itemId, itemUpdateDto);

        // Проверки
        assertNotNull(result);
        assertEquals(updatedName, result.getName(), "Item name should be updated to the new name");
        verify(itemStorage).save(item); // Проверяем, был ли предмет сохранен после обновления
        verify(itemMapper).toDto(item); // Проверяем, был ли результат корректно преобразован в DTO
    }

    @Test
    void updateItemShouldUpdateDescriptionIfProvided() {
        // Подготовка данных
        Long userId = 1L;
        Long itemId = 1L;
        String originalDescription = "Powerful tool";
        String updatedDescription = "Updated powerful tool";
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(null, updatedDescription, null);

        User owner = User.builder().id(userId).name("John Doe").email("john@example.com").build();
        Item item = Item.builder()
                .id(itemId)
                .name("Drill")
                .description(originalDescription)
                .available(true)
                .owner(owner)
                .build();

        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        when(itemStorage.save(item)).thenReturn(item);
        ItemDto returnedDto = new ItemDto(itemId, "Drill", updatedDescription, true, null);
        when(itemMapper.toDto(item)).thenReturn(returnedDto);

        // Вызов метода
        ItemDto result = itemService.updateItem(userId, itemId, itemUpdateDto);

        // Проверки
        assertNotNull(result);
        assertEquals(updatedDescription, result.getDescription(), "Item description should be updated to the new description");
        verify(itemStorage).save(item); // Проверяем, был ли предмет сохранен после обновления
        verify(itemMapper).toDto(item); // Проверяем, был ли результат корректно преобразован в DTO
    }

    @Test
    void updateItemShouldUpdateAvailabilityIfProvided() {
        // Подготовка данных
        Long userId = 1L;
        Long itemId = 1L;
        Boolean originalAvailability = true; // Исходное значение доступности
        Boolean updatedAvailability = false; // Обновленное значение доступности
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(null, null, updatedAvailability);

        User owner = User.builder().id(userId).name("John Doe").email("john@example.com").build();
        Item item = Item.builder()
                .id(itemId)
                .name("Drill")
                .description("Powerful tool")
                .available(originalAvailability)
                .owner(owner)
                .build();

        // Мокирование зависимостей
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        when(itemStorage.save(item)).thenReturn(item);
        ItemDto returnedDto = new ItemDto(itemId, "Drill", "Powerful tool", updatedAvailability, null);
        when(itemMapper.toDto(item)).thenReturn(returnedDto);

        // Вызов метода
        ItemDto result = itemService.updateItem(userId, itemId, itemUpdateDto);

        // Проверки
        assertNotNull(result);
        assertEquals(updatedAvailability, result.getAvailable(), "Item availability should be updated to the new value");
        verify(itemStorage).save(item); // Проверяем, был ли предмет сохранен после обновления
        verify(itemMapper).toDto(item); // Проверяем, был ли результат корректно преобразован в DTO
    }

    @Test
    void updateItemShouldThrowEntityNotFoundExceptionIfItemNotFound() {
        // Подготовка данных
        Long userId = 1L;
        Long itemId = 1L;  // ID предмета, который мы пытаемся найти
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("Drill", "Powerful tool", true);

        // Настройка мока для имитации отсутствия предмета
        when(itemStorage.findById(itemId)).thenReturn(Optional.empty());

        // Ожидаем, что будет выброшено конкретное исключение
        assertThrows(EntityNotFoundException.class,
                () -> itemService.updateItem(userId, itemId, itemUpdateDto),
                "Should throw EntityNotFoundException if item not found");

        // Проверки на вызовы методов
        verify(itemStorage).findById(itemId);
        verifyNoMoreInteractions(itemStorage);
    }

}
