package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dao.ItemRequestStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService{
    private final UserStorage userStorage;
    private final ItemRequestStorage itemRequestStorage;
    private final ItemStorage itemStorage;
    private final BookingStorage bookingStorage;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto) {
        final ItemRequest itemRequest = itemRequestMapper.dtoToItemRequest(itemRequestDto);
        final ItemRequest addedItemRequest = itemRequestStorage.add(itemRequest);
        log.info("Добавление нового ItemRequest: {}", addedItemRequest);
        return itemRequestMapper.itemRequestToDto(itemRequestStorage.findById(addedItemRequest.getId()));
    }

    @Override
    public ItemRequestDto updateItemRequest(ItemRequestDto updatedItemRequestDto) {
        final ItemRequest updatedItemRequest = itemRequestMapper.dtoToItemRequest(updatedItemRequestDto);
        final long itemId = updatedItemRequest.getId();
        itemRequestStorage.update(updatedItemRequest);
        log.info("Обновление ItemRequest с id {}: {}", itemId, updatedItemRequest);
        return itemRequestMapper.itemRequestToDto(itemRequestStorage.findById(itemId));
    }

    @Override
    public Collection<ItemRequestDto> getAllItemRequests() {
        log.info("Получение списка всех ItemRequests.");
        return itemRequestStorage.findAll().stream()
                .map(itemRequestMapper::itemRequestToDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ItemRequestDto getItemRequestById(long itemRequestId) {
        final ItemRequest itemRequest = itemRequestStorage.findById(itemRequestId);
        log.info("ItemRequest с id {} найден.", itemRequestId);
        return itemRequestMapper.itemRequestToDto(itemRequest);
    }

    @Override
    public void removeItemRequest(long itemRequestId) {
        itemRequestStorage.remove(itemRequestId);
        log.info("ItemRequest с id {} удалено.", itemRequestId);
    }
}
