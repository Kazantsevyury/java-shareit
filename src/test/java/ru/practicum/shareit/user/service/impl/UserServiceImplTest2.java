package ru.practicum.shareit.user.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ExceptionFactory;
import ru.practicum.shareit.exception.exceptions.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest2 {

    @Mock
    private UserStorage userStorage;
    @Mock
    private ExceptionFactory exceptionFactory;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testAddUserWithValidData() {
        // Подготовка входных данных
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setEmail("user@example.com");

        // Настройка поведения моков
        when(userStorage.findByEmail(anyString())).thenReturn(Optional.empty()); // Эмуляция отсутствия пользователя с таким email
        when(userStorage.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Возвращение сохраненного пользователя

        // Выполнение теста
        UserDto result = userService.addUser(userCreateDto);

        // Проверки
        assertNotNull(result);
        assertEquals("user@example.com", result.getEmail());

        // Подтверждение взаимодействия с моками
        verify(userStorage).findByEmail("user@example.com");
        verify(userStorage).save(any(User.class));
    }

    @Test
    public void testAddUserWithDuplicateEmailThrowsException() {
        // Подготовка
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setEmail("duplicate@example.com");
        User existingUser = new User();
        existingUser.setEmail("duplicate@example.com");

        // Настройка моков
        when(userStorage.findByEmail("duplicate@example.com")).thenReturn(Optional.of(existingUser));

        // Ожидаемое исключение
        Exception thrown = assertThrows(EntityAlreadyExistsException.class, () -> userService.addUser(userCreateDto));

        // Проверки
        assertEquals("Email уже используется другим пользователем.", thrown.getMessage());

        // Подтверждение взаимодействия с моками
        verify(userStorage).findByEmail("duplicate@example.com");
    }

    @Test
    public void testUpdateUserWithValidData() {
        // Подготовка данных
        Long userId = 1L;
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();
        User existingUser = User.builder()
                .id(userId)
                .name("Original Name")
                .email("original@example.com")
                .build();

        // Настройка моков
        when(userStorage.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userStorage.findByEmail("updated@example.com")).thenReturn(Optional.empty());
        when(userStorage.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Выполнение теста
        UserDto result = userService.updateUser(userId, userUpdateDto);

        // Проверки
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("updated@example.com", result.getEmail());

        // Подтверждение взаимодействия с моками
        verify(userStorage).findById(userId);
        verify(userStorage).findByEmail("updated@example.com");
        verify(userStorage).save(any(User.class));
    }

    @Test
    public void testUpdateNonExistentUserThrowsException() {
        // Подготовка данных
        Long userId = 1L;
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        // Настройка моков
        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        // Ожидаемое исключение
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(userId, userUpdateDto);
        });

        // Проверка сообщения исключения
        assertEquals("Пользователь с ID " + userId + " не найден", exception.getMessage());

        // Подтверждение взаимодействия с моками
        verify(userStorage).findById(userId);
        verify(userStorage, never()).findByEmail(anyString());
        verify(userStorage, never()).save(any(User.class));
    }

    @Test
    public void testUpdateUserWithEmailNull() {
        Long userId = 1L;
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("Updated Name")
                .email(null)
                .build();
        User existingUser = User.builder()
                .id(userId)
                .name("Original Name")
                .email("original@example.com")
                .build();

        when(userStorage.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userStorage.save(any(User.class))).thenReturn(existingUser);

        UserDto result = userService.updateUser(userId, userUpdateDto);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("original@example.com", result.getEmail());

        verify(userStorage).save(any(User.class));
    }

    @Test
    public void testUpdateUserWithEmailEmpty() {
        // Подготовка данных
        Long userId = 1L;
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("Updated Name")
                .build();
        User existingUser = User.builder()
                .id(userId)
                .name("Original Name")
                .email("original@example.com")
                .build();

        // Настройка моков
        when(userStorage.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userStorage.save(existingUser)).thenReturn(existingUser);

        // Выполнение теста
        UserDto result = userService.updateUser(userId, userUpdateDto);

        // Проверки
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("original@example.com", result.getEmail());

        // Подтверждение взаимодействия с моками
        verify(userStorage).findById(userId);
        verify(userStorage).save(existingUser);
    }

    @Test
    public void testUpdateUserWithExistingEmailThrowsException() {
        // Подготовка данных
        Long userId = 1L;
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("Updated Name")
                .email("usedemail@example.com")
                .build();
        User existingUser = User.builder()
                .id(userId)
                .name("Original Name")
                .email("original@example.com")
                .build();
        User anotherUserWithSameEmail = User.builder()
                .id(2L)
                .name("Another Name")
                .email("usedemail@example.com")
                .build();

        // Настройка моков
        when(userStorage.findById(eq(userId))).thenReturn(Optional.of(existingUser));
        when(userStorage.findByEmail(eq("usedemail@example.com"))).thenReturn(Optional.of(anotherUserWithSameEmail));

        // Вызов метода и проверка выброса исключения
        Exception exception = assertThrows(EntityAlreadyExistsException.class, () -> userService.updateUser(userId, userUpdateDto));

        // Проверка сообщения исключения
        assertEquals("Email уже используется другим пользователем.", exception.getMessage());

        // Подтверждение взаимодействия с моками
        verify(userStorage).findById(userId);
        verify(userStorage).findByEmail("usedemail@example.com");
    }

    @Test
    public void testFindUserByIdWithValidId() {
        // Подготовка данных
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("test@example.com");

        // Настройка моков
        when(userStorage.findById(userId)).thenReturn(Optional.of(user));

        // Выполнение теста
        UserDto result = userService.findUserById(userId);

        // Проверки
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());

        // Подтверждение взаимодействия с моками
        verify(userStorage).findById(userId);
    }

    @Test
    public void testFindUserByIdWithInvalidIdThrowsException() {
        // Подготовка данных
        Long userId = 1L;

        // Настройка моков
        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        // Ожидаемое исключение
        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.findUserById(userId));

        // Проверка сообщения исключения
        assertEquals("Пользователь с ID " + userId + " не найден", exception.getMessage());

        // Подтверждение взаимодействия с моками
        verify(userStorage).findById(userId);
    }

}
