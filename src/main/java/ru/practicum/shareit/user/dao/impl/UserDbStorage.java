package ru.practicum.shareit.user.dao.impl;

import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public class UserDbStorage implements UserStorage {

    @Override
    public User add(User user) {
        return null;
    }

    @Override
    public void remove(Long id) {
    }

    @Override
    public void update(User user) {
    }

    @Override
    public Collection<User> findAll() {
        return null;
    }

    @Override
    public User findById(Long id) {
        return null;
    }

    @Override
    public boolean existsById(Long userId) {
        return false;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }
}