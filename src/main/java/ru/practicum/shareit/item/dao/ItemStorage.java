package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.Dao;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@SuppressWarnings("checkstyle:Regexp")
public interface ItemStorage extends Dao<Item> {
    Collection<Item> findAllByOwnerId(Long ownerId);

    Collection<Item> searchAvailableItemsByText(String text);

}
