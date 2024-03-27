package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.ExceptionFactory;
import ru.practicum.shareit.exception.exceptions.AccessDeniedException;
import ru.practicum.shareit.exception.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exception.exceptions.UserNotFoundException;
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
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final CommentStorage commentStorage;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingStorage bookingStorage;

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemCreateDto itemDto) {
        User owner = userStorage.findById(userId)
                .orElseThrow(() -> ExceptionFactory.userNotFoundException("User not found with id: " + userId));
        Item item = itemMapper.fromItemCreateDto(itemDto);
        item.setOwner(owner);
        Item savedItem = itemStorage.save(item);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemUpdateDto itemUpdateDto) {
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> ExceptionFactory.entityNotFound("Item", itemId));

        if (!item.getOwner().getId().equals(userId)) {
            throw ExceptionFactory.accessDenied("User is not the owner of the item.");
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
    @Transactional
    public CommentDto addCommentToItem(Long userId, Long itemId, CommentCreateDto commentDto) {
        if (!bookingStorage.hasUserRentedItem(userId, itemId)) {
            throw new AccessDeniedException("User did not rent the item or rental period has not ended.");
        }

        User author = userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + itemId));

        // Создание и сохранение комментария
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        Comment savedComment = commentStorage.save(comment);

        return commentMapper.toCommentDto(savedComment);
    }


    @Override
    public void removeItem(Long itemId) {
        itemStorage.deleteById(itemId);
    }

}