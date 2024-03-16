package ru.practicum.shareit.user.dao.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserInMemoryStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long currentId = Long.valueOf(0);

    @Override
    public User add(User user) {
        user.setId(++currentId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void remove(Long id) {
        users.remove(id);
    }

    @Override
    public void update(User user) {
            users.replace(user.getId(), user);
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }

    @Override
    public boolean existsById(Long userId) {
        if (users.containsKey(userId)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }
}
