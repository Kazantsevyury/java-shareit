package ru.practicum.shareit.user.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

/*
    @Test
    void updateUser_UserFoundAndNameNotNullEmailNotNull_ShouldUpdateNameAndEmail() {
        when(userStorage.findById(userId))
                .thenReturn(Optional.of(user));

        userService.updateUser(userId, updateDto);

        verify(userStorage).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertThat(savedUser.getName(), is(updateDto.getName()));
        assertThat(savedUser.getEmail(), is(updateDto.getEmail()));

        verify(userStorage, times(1)).findById(userId);
        verify(userStorage, times(1)).save(savedUser);
        verify(userMapper, times(1)).toUserDto(savedUser);
    }

    @Test
    void updateUser_UserNotFound_ShouldThrowNotFoundException() {
        when(userStorage.findById(userId))
                .thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(userId, updateDto));
        assertThat(e.getMessage(), is("Пользователь с ID " + userId + "' не найден."));

        verify(userStorage, times(1)).findById(userId);
        verify(userStorage, never()).save(any());
        verify(userMapper, never()).toUserDto(any());
    }

    @Test
    void updateUser_UserFoundAndNameNullEmailNotNull_ShouldUpdateOnlyEmail() {
        updateDto.setName(null);
        when(userStorage.findById(userId))
                .thenReturn(Optional.of(user));

        userService.updateUser(userId, updateDto);

        verify(userStorage).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertThat(savedUser.getName(), is(user.getName()));
        assertThat(savedUser.getEmail(), is(updateDto.getEmail()));

        verify(userStorage, times(1)).findById(userId);
        verify(userStorage, times(1)).save(savedUser);
        verify(userMapper, times(1)).toUserDto(savedUser);
    }

    @Test
    void updateUser_UserFoundAndNameNotNullEmailNull_ShouldUpdateOnlyName() {
        updateDto.setEmail(null);
        when(userStorage.findById(userId))
                .thenReturn(Optional.of(user));

        userService.updateUser(userId, updateDto);

        verify(userStorage).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertThat(savedUser.getName(), is(updateDto.getName()));
        assertThat(savedUser.getEmail(), is(user.getEmail()));

        verify(userStorage, times(1)).findById(userId);
        verify(userStorage, times(1)).save(savedUser);
        verify(userMapper, times(1)).toDto(savedUser);
    }

    @Test
    void updateUser_UserFoundAndNameNullEmailNull_ShouldNotUpdateAnyFields() {
        updateDto.setEmail(null);
        updateDto.setName(null);
        when(userStorage.findById(userId))
                .thenReturn(Optional.of(user));

        userService.updateUser(userId, updateDto);

        verify(userStorage).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertThat(savedUser.getName(), is(user.getName()));
        assertThat(savedUser.getEmail(), is(user.getEmail()));

        verify(userStorage, times(1)).findById(userId);
        verify(userStorage, times(1)).save(savedUser);
        verify(userMapper, times(1)).toDto(savedUser);
    }

    @Test
    void findUserById_UserFound_ShouldReturnDto() {
        when(userStorage.findById(userId))
                .thenReturn(Optional.of(user));

        userService.findUserById(userId);

        verify(userStorage, times(1)).findById(userId);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void findUserById_UserNotFound_ShouldThrowNotFoundException() {
        when(userStorage.findById(userId))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> userService.findUserById(userId));
        assertThat(e.getMessage(), is("Пользователь с id '" + userId + "' не найден."));

        verify(userStorage, times(1)).findById(userId);
        verify(userMapper, never()).toDto(user);
    }

    @Test
    void findAllUsers_ShouldReturnList() {
        when(userStorage.findAll())
                .thenReturn(List.of(user));

        userService.findAllUsers();

        verify(userStorage, times(1)).findAll();
        verify(userMapper, times(1)).toDtoList(List.of(user));
    }

    @Test
    void deleteUserById() {
        userService.deleteUserById(userId);

        verify(userStorage, times(1)).deleteById(userId);
    } */
}