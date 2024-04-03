package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface ItemRequestMapper {

    ItemRequest toModel(AddItemRequestDto addItemRequestDto);


    ItemRequestDto toDto(ItemRequest itemRequest);

    List<ItemRequestDto> toDtoList(List<ItemRequest> requests);
}
