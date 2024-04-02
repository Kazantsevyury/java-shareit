package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private static final String DEFAULT_PAGE_SIZE = "10";

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addNewItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody @Valid AddItemRequestDto addItemRequestDto) {
        return itemRequestService.addNewItemRequest(userId, addItemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllItemRequestsFromUser(@RequestHeader("X-Sharer-User-id") Long userId) {
        return itemRequestService.getAllItemRequestsFromUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAvailableItemRequests(@RequestHeader("X-Sharer-User-id") Long userId,
                                                         @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) @Positive Integer size) {
        return itemRequestService.getAvailableItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-id") Long userId,
                                             @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
