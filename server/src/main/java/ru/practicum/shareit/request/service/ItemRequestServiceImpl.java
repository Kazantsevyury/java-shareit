package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.shared.OffsetPageRequest;
import ru.practicum.shareit.shared.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestStorage itemRequestStorage;
    private final UserStorage userStorage;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDto addNewItemRequest(final Long userId, final AddItemRequestDto addItemRequestDto) {
        final User requester = findUser(userId);
        final ItemRequest itemRequest = itemRequestMapper.toModel(addItemRequestDto);
        itemRequest.setRequester(requester);
        final ItemRequest savedRequest = itemRequestStorage.save(itemRequest);
        return itemRequestMapper.toDto(savedRequest);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequestsFromUser(final Long userId) {
        findUser(userId);
        final List<ItemRequest> requests = itemRequestStorage.findRequestsFromUser(userId);
        return itemRequestMapper.toDtoList(requests);
    }

    @Override
    public List<ItemRequestDto> getAvailableItemRequests(final Long userId, final Long from, final Integer size) {
        findUser(userId);
        final OffsetPageRequest pageRequest = OffsetPageRequest.of(from, size);
        final Page<ItemRequest> requests = itemRequestStorage.findAvailableRequests(userId, pageRequest);
        return itemRequestMapper.toDtoList(requests.getContent());
    }

    @Override
    public ItemRequestDto getItemRequestById(final Long userId, final Long requestId) {
        findUser(userId);
        final ItemRequest itemRequest = itemRequestStorage.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id '" + requestId + "' не найден."));
        return itemRequestMapper.toDto(itemRequest);
    }

    private User findUser(final Long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id '" + userId + "' не найден."));
    }
}
