package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.Dao;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserStorage extends Dao<User> {

    boolean existsById(Long userId);

    Optional<User> findByEmail(String email);
}
