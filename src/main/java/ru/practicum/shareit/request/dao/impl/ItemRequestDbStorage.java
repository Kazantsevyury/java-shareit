package ru.practicum.shareit.request.dao;

import ru.practicum.shareit.request.ItemRequest;

import java.util.Collection;

public class ItemRequestDbStorage implements ItemRequestStorage {

    @Override
    public ItemRequest add(ItemRequest itemRequest) {
        throw new UnsupportedOperationException("Операция пока не поддерживается.");
    }

    @Override
    public void remove(Long id) {
        throw new UnsupportedOperationException("Операция пока не поддерживается.");
    }

    @Override
    public void update(ItemRequest itemRequest) {
        throw new UnsupportedOperationException("Операция пока не поддерживается.");
    }

    @Override
    public Collection<ItemRequest> findAll() {
        throw new UnsupportedOperationException("Операция пока не поддерживается.");
    }

    @Override
    public ItemRequest findById(Long id) {
        throw new UnsupportedOperationException("Операция пока не поддерживается.");
    }
}
