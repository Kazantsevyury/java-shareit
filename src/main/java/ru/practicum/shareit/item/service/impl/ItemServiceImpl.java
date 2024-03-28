package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ExceptionFactory;
import ru.practicum.shareit.exception.exceptions.AccessDeniedException;
import ru.practicum.shareit.exception.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.ItemBookingFacadeImpl;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.comment.CommentCreateDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final CommentStorage commentStorage;
    @Lazy
    private final UserService userService;
    @Lazy

    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemCreateDto itemDto) {

        UserDto ownerDTO = userService.findUserById(userId);
        Item item = itemMapper.fromItemCreateDto(itemDto);
        item.setOwner(userMapper.fromUserDto(ownerDTO));
        Item savedItem = itemStorage.save(item);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemUpdateDto itemUpdateDto) {
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> ExceptionFactory.entityNotFound("Item", itemId));

        if (!item.getOwner().getId().equals(userId)) {
            throw ExceptionFactory.accessDenied("User не является хозяином. ");
        }
        itemMapper.updateItemFromItemUpdateDto(itemUpdateDto, item);
        Item updatedItem = itemStorage.save(item);
        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemResponseDto findItemById(Long userId, Long itemId) {
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> ExceptionFactory.entityNotFound("Item", itemId));
        List<CommentDto> comments = commentStorage.findAllByItemId(itemId)
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
        return itemMapper.toItemResponseDto(item, comments);
    }

    @Override
    public List<ItemResponseDto> findAllItemsByUserId(Long userId) {
        List<Item> items = itemStorage.findAllByOwnerIdOrderById(userId);
        return items.stream()
                .map(item -> {
                    List<CommentDto> comments = commentStorage.findAllByItemId(item.getId())
                            .stream()
                            .map(commentMapper::toCommentDto)
                            .collect(Collectors.toList());
                    return itemMapper.toItemResponseDto(item, comments);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemStorage.searchAvailableItemsByText(text.toLowerCase())
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }




    @Override
    public void removeItem(Long itemId) {
        itemStorage.deleteById(itemId);
    }

    @Override
    public Item getPureItemById(Long id){
        Item item = itemStorage.findById(id)
                .orElseThrow(() -> ExceptionFactory.entityNotFound("Item", id));
        return item;
    }

}