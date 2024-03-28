package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.comment.CommentCreateDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemBookingFacade itemBookingFacade;

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestBody @Valid ItemCreateDto itemCreateDto,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemDto createdItem = itemBookingFacade.addItem(userId, itemCreateDto);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long itemId,
                                              @RequestBody @Valid ItemUpdateDto itemUpdateDto,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemDto updatedItem = itemService.updateItem(userId, itemId, itemUpdateDto);
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> getItemById(@PathVariable Long itemId,
                                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemResponseDto itemResponseDto = itemService.findItemById(userId, itemId);
        return ResponseEntity.ok(itemResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemResponseDto> items = itemService.findAllItemsByUserId(userId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        List<ItemDto> items = itemService.searchItems(text);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long itemId,
                                       @RequestBody @Valid CommentCreateDto commentDto) {
        return itemBookingFacade.addCommentToItem(userId, itemId, commentDto);
    }
}