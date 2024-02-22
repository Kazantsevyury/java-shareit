package ru.practicum.shareit;

import java.util.Collection;
import java.util.Optional;

public interface Dao<T> {
    T add(T t);
    void remove(Long id);
    void update(T t);
    Collection<T> findAll();
    T findById(Long id);
}
