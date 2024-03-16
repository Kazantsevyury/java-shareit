package ru.practicum.shareit.item.dao.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemInMemoryStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private Long currentId = Long.valueOf(0);

    @Override
    public Item add(Item item) {
        item.setId(++currentId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void remove(Long id) {
        items.remove(id);
    }

    @Override
    public void update(Item item) {
        if (items.containsKey(item.getId())) {
            items.put(item.getId(), item);
        }
    }

    @Override
    public Collection<Item> findAll() {
        return items.values();
    }

    @Override
    public Item findById(Long id) {
        return items.get(id);
    }

    @Override
    public Collection<Item> findAllByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> searchAvailableItemsByText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String lowerCaseText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toLowerCase().contains(lowerCaseText) ||
                                item.getDescription().toLowerCase().contains(lowerCaseText)))
                .collect(Collectors.toList());
    }

}
