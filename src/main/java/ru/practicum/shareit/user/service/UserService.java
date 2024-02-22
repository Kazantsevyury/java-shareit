package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto addUser(UserDto userDto);

    UserDto updateUser(Long id, UserDto userDto);

    Collection<UserDto> getAllUsers();

    UserDto getUserById(Long userId);

    void removeUser(Long userId);

    boolean existsById(Long userId);

}
