package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    ItemRequestDto itemRequestToDto(ItemRequest itemRequest);

    ItemRequest dtoToItemRequest(ItemRequestDto itemRequestDto);

}
