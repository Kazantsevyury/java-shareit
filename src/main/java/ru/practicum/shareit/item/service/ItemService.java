package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    Item addItem(Item item);
    ItemDto updateItem(ItemDto itemDto);
    Collection<Item> getAllItems();
    Item getItemById(Long itemId);
    void removeItem(Long itemId);
    List<ItemDto> getAllItemsByOwner(Long userId);
    List<ItemDto> searchAvailableItems(String text);
    }
