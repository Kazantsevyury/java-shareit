package ru.practicum.shareit.user.dao.impl;

import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserDbStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long currentId = Long.valueOf(1);

    @Override
    public User add(User user) {
        user.setId(currentId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void remove(Long id) {
        users.remove(id);
    }

    @Override
    public void update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        }
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }
}
