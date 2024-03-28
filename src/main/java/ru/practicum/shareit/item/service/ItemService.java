package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.comment.CommentCreateDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemCreateDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemUpdateDto itemUpdateDto);

    ItemResponseDto findItemById(Long userId, Long itemId);

    List<ItemResponseDto> findAllItemsByUserId(Long userId);

    List<ItemDto> searchItems(String text);

    //CommentDto addCommentToItem(Long userId, Long itemId, CommentCreateDto commentDto);

    void removeItem(Long itemId);
    Item getPureItemById(Long itemId);
}

