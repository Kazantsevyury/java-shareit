package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestStorage itemRequestStorage;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDto addNewItemRequest(final Long userId, final AddItemRequestDto addItemRequestDto) {
        userService.findUserById(userId);
        var requester = userService.getPureUserById(userId);

        final ItemRequest itemRequest = itemRequestMapper.toModel(addItemRequestDto);
        itemRequest.setRequester(requester);
        final ItemRequest savedRequest = itemRequestStorage.save(itemRequest);
        return itemRequestMapper.toDto(savedRequest);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequestsFromUser(final Long userId) {
        userService.findUserById(userId);
        final List<ItemRequest> requests = itemRequestStorage.findRequestsFromUser(userId);
        List<ItemRequestDto> requestDtos = itemRequestMapper.toDtoList(requests);

        for (ItemRequestDto requestDto : requestDtos) {
            if (requestDto.getItems() != null) {
                for (ItemDto itemDto : requestDto.getItems()) {
                    itemDto.setRequestId(requestDto.getId());
                }
            }
        }

        return requestDtos;
    }

    @Override
    public List<ItemRequestDto> getAvailableItemRequests(final Long userId, final Long offset, final Integer size) {
        userService.findUserById(userId);
        int page = (int) (offset / size);
        Pageable pageable = PageRequest.of(page, size, Sort.unsorted());

        final Page<ItemRequest> requests = itemRequestStorage.findAvailableRequests(userId, pageable);
        return itemRequestMapper.toDtoList(requests.getContent());
    }

    @Override
    public ItemRequestDto getItemRequestById(final Long userId, final Long requestId) {
        userService.findUserById(userId);
        final ItemRequest itemRequest = itemRequestStorage.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Запрос с id '" + requestId + "' не найден."));
        return itemRequestMapper.toDto(itemRequest);
    }

    @Override
    public ItemRequest getPureItemRequestById(Long itemRequestId) {
        return itemRequestStorage.getReferenceById(itemRequestId);
    }

}

