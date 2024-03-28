package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDto toItemDto(Item item);

    @Mapping(target = "owner.id", source = "ownerId")
    Item fromItemCreateDto(ItemCreateDto itemCreateDto);

    @Mappings({
            @Mapping(target = "owner", source = "item.owner"),
            @Mapping(target = "comments", source = "comments")
    })
    ItemResponseDto toItemResponseDto(Item item, List<CommentDto> comments);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "name", target = "name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE),
            @Mapping(source = "description", target = "description", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE),
            @Mapping(source = "available", target = "available", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    })
    void updateItemFromItemUpdateDto(ItemUpdateDto itemUpdateDto, @MappingTarget Item item);

    @Mappings({
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "description", source = "description"),
            @Mapping(target = "available", source = "available"),
            @Mapping(target = "ownerId", source = "owner.id")
    })
    ItemCreateDto toItemCreateDto(Item item);



}
