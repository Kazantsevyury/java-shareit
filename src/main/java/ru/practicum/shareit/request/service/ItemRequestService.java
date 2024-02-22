package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto);

    ItemRequestDto updateItemRequest(ItemRequestDto itemRequestDto);

    Collection<ItemRequestDto> getAllItemRequests();

    ItemRequestDto getItemRequestById(long itemRequestId);

    void removeItemRequest(long itemRequestId);
}
