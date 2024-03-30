package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.comment.AddCommentDto;
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
    public ResponseEntity<ItemDto> addItem(@RequestBody @Valid ItemDto itemCreateDto,
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
    public ResponseEntity<GetItemDto> getItemById(@PathVariable Long itemId,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        GetItemDto getItemDto = itemService.findItemById(userId, itemId);
        return ResponseEntity.ok(getItemDto);
    }

    @GetMapping
    public ResponseEntity<List<GetItemDto>> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<GetItemDto> items = itemService.findAllItemsByUserId(userId);
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
                                       @RequestBody @Valid AddCommentDto commentDto) {
        return itemBookingFacade.addCommentToItem(userId, itemId, commentDto);
    }
}