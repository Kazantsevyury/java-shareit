package ru.practicum.shareit.item.dao.impl;

import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public class ItemDbStorage implements ItemStorage {

    @Override
    public Item add(Item item) {
        throw new UnsupportedOperationException("Операция пока не поддерживается.");
    }

    @Override
    public void remove(Long id) {
        throw new UnsupportedOperationException("Операция пока не поддерживается.");
    }

    @Override
    public void update(Item item) {
        throw new UnsupportedOperationException("Операция пока не поддерживается.");
    }

    @Override
    public Collection<Item> findAll() {
        throw new UnsupportedOperationException("Операция пока не поддерживается.");
    }

    @Override
    public Item findById(Long id) {
        throw new UnsupportedOperationException("Операция пока не поддерживается.");
    }

    @Override
    public Collection<Item> findAllByOwnerId(Long ownerId) {
        throw new UnsupportedOperationException("Операция пока не поддерживается.");
    }

    @Override
    public Collection<Item> searchAvailableItemsByText(String text) {
        throw new UnsupportedOperationException("Операция пока не поддерживается.");
    }
}
