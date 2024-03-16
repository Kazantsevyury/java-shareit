package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.ExceptionFactory;
import ru.practicum.shareit.exception.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.ItemDtoValidator;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemValidator;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserValidator;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;
    private final ItemDtoValidator itemDtoValidator;
    private final UserService userService;
    private final ItemValidator itemValidator;
    private final UserValidator userValidator;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        userValidator.verifyUserExists(userId);

        Item item = itemMapper.itemDtoToItem(itemDto);

        item.setOwner(userId);
        itemValidator.validate(item);
        Item savedItem = itemStorage.add(item);
        return itemMapper.itemToItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId) {
        userValidator.verifyUserExists(userId);
        Item existingItem = itemStorage.findById(itemId);
        if (existingItem == null || !existingItem.getOwner().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет доступа к этому предмету или предмет не найден");
        }

        boolean updated = false;
        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
            updated = true;
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
            updated = true;
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
            updated = true;
        }

        if (!updated) {
            throw ExceptionFactory.invalidData("Нет данных для обновления");
        }

        itemStorage.update(existingItem);
        return itemMapper.itemToItemDto(existingItem);
    }


    @Override
    public Collection<Item> getAllItems() {
        log.info("Получение списка всех предметов.");
        return itemStorage.findAll().stream()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Item getItemById(Long itemId) {
        final Item item = itemStorage.findById(itemId);
        if (item == null) {
            throw new EntityNotFoundException("Предмет с id " + itemId + " не найден.");
        }
        log.info("Предмет с id {} найден.", itemId);
        return item;
    }

    @Override
    public void removeItem(Long itemId) {
        final Item item = itemStorage.findById(itemId);
        if (item == null) {
            throw new EntityNotFoundException("Предмет с id " + itemId + " не найден для удаления.");
        }
        itemStorage.remove(itemId);
        log.info("Предмет с id {} удален.", itemId);
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long userId) {
        return itemStorage.findAllByOwnerId(userId).stream()
                .map(itemMapper::itemToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItems(String text) {
        return itemStorage.searchAvailableItemsByText(text).stream()
                .map(itemMapper::itemToItemDto)
                .collect(Collectors.toList());
    }

}
