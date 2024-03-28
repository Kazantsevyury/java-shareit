package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    UserDto addUser(UserCreateDto userCreateDto);

    UserDto updateUser(Long id, UserUpdateDto userUpdateDto);

    Collection<UserDto> findAllUsers();

    UserDto findUserById(Long userId);

    void deleteUserById(Long userId);
    User getPureUserById(Long userId);

}
