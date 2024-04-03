package ru.practicum.shareit.item.mapper;


import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(source = "request.id", target = "requestId")
    ItemDto toDto(Item item);

    Item toModel(ItemDto itemDto);

    GetItemDto toWithBookingsDto(Item item);

    List<GetItemDto> toWithBookingsDtoList(List<Item> itemList);

    default GetItemDto toGetItemDto(Item item, ShortBookingDto lastBooking, ShortBookingDto nextBooking) {
        return GetItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }
    @AfterMapping
    default void handleRequest(@MappingTarget ItemDto target, Item source) {
        if (source.getRequest() != null) {
            target.setRequestId(source.getRequest().getId());
        }
    }
}
