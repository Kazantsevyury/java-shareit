package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserValidator;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final UserValidator userValidator;

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestBody @Valid ItemDto itemDto,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemDto createdItem = itemService.addItem(itemDto, userId);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long itemId,
                                              @RequestBody @Valid ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        userValidator.verifyUserExists(userId);
        ItemDto updatedItem = itemService.updateItem(itemDto, userId, itemId);
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId) {
        ItemDto itemDto = itemMapper.itemToItemDto(itemService.getItemById(itemId));
        if (itemDto != null) {
            return ResponseEntity.ok(itemDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        userValidator.verifyUserExists(userId);
        List<ItemDto> items = itemService.getAllItemsByOwner(userId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        if (text.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        List<ItemDto> items = itemService.searchAvailableItems(text);
        return ResponseEntity.ok(items);
    }

}
