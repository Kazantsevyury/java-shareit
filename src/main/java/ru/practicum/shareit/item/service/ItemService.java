package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId);

    Collection<Item> getAllItems();

    Item getItemById(Long itemId);

    void removeItem(Long itemId);

    List<ItemDto> getAllItemsByOwner(Long userId);

    List<ItemDto> searchAvailableItems(String text);

    }
