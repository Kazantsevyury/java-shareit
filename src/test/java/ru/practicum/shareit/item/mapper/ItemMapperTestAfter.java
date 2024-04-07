package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ItemMapperTestAfter {
        private final ItemMapper mapper = Mappers.getMapper(ItemMapper.class);

        @Test
        void testHandleRequest() {
            // Создаем исходный объект Item
            Item item = Item.builder()
                    .id(1L)
                    .name("Test Item")
                    .description("Test Description")
                    .available(true)
                    .request(new ItemRequest()) // Инициализация объекта ItemRequest, если необходимо
                    .build();


            // Создаем объект ItemDto без requestId
            ItemDto itemDtoWithoutRequestId = mapper.toDto(item);

            // Вызываем метод handleRequest для преобразования ItemDto с requestId
            mapper.handleRequest(itemDtoWithoutRequestId, item);

            // Проверяем, что requestId был добавлен к ItemDto
            assertEquals(item.getRequest().getId(), itemDtoWithoutRequestId.getRequestId());
        }
    }

