package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ExceptionFactory;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import org.springframework.validation.BindException;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final ItemDtoValidator itemDtoValidator;

    @PostMapping
    public ResponseEntity<?> addItem(@RequestBody @Valid ItemDto itemDto,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        BindException errors = new BindException(itemDto, "itemDto");

        log.info("Попытка добавления нового предмета с названием: {} и описанием: {} пользователем с ID: {}",
                itemDto.getName(), itemDto.getDescription(), userId);

        if (!userService.existsById(userId)) {
            log.warn("Пользователь с ID: {} не найден", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        itemDtoValidator.validate(itemDto, errors);

        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }

        if (itemDto.getAvailable() == null || Boolean.FALSE.equals(itemDto.getAvailable())) {
            log.warn("Предмет с названием: {} не может быть создан, так как он указан как недоступный для аренды", itemDto.getName());
            return ResponseEntity.badRequest().body("Необходимо указать доступность предмета как true для создания.");
        }

        itemDto.setOwner(userId);
        Item createdItem = itemService.addItem(itemMapper.itemDtoToItem(itemDto));
        log.info("Предмет с названием: {} успешно создан с ID: {}", itemDto.getName(), createdItem.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(itemMapper.itemToItemDto(createdItem));
    }


    @PatchMapping("/{itemId}")
    public ResponseEntity<?> updateItem(@PathVariable Long itemId,
                                        @RequestBody @Valid ItemDto itemDto,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Неверный идентификатор пользователя.");
        }

        if (!userService.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь с ID: " + userId + " не найден.");
        }

        Item existingItem = itemService.getItemById(itemId);
        if (existingItem == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Предмет с ID: " + itemId + " не найден.");
        }

        if (!existingItem.getOwner().equals(userId)) {
            throw ExceptionFactory.accessDenied("Доступ запрещен. Вы не являетесь владельцем данного предмета.");
        }

        itemDto.setId(itemId);
        ItemDto updatedItem = itemService.updateItem(itemDto);
        return ResponseEntity.ok(updatedItem);
    }


    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId) {
        ItemDto itemDto = itemMapper.itemToItemDto(itemService.getItemById(itemId));
        if (itemDto != null) {
            return ResponseEntity.ok(itemDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        if (!userService.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<ItemDto> items = itemService.getAllItemsByOwner(userId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (text.trim().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<ItemDto> items = itemService.searchAvailableItems(text);
        return ResponseEntity.ok(items);
    }


}
