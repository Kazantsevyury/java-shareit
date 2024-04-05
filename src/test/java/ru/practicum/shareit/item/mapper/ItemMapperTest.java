package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemMapperTest {

    private ItemMapper itemMapper;

    @BeforeAll
    void init() {
        itemMapper = new ItemMapperImpl();
    }

    @Test
    void toDto_mapNull_ShouldReturnNull() {
        ItemDto dto = itemMapper.toDto(null);

        assertThat(dto, nullValue());
    }

    @Test
    void toModel_mapNull_ShouldReturnNull() {
        Item item = itemMapper.toModel(null);

        assertThat(item, nullValue());
    }

    @Test
    void toWithBookingsDto_mapNull_ShouldReturnNull() {
        GetItemDto getItemDto = itemMapper.toWithBookingsDto(null);

        assertThat(getItemDto, nullValue());
    }

    @Test
    void toWithBookingsDtoList_mapNull_ShouldReturnNull() {
        List<GetItemDto> getItemDtos = itemMapper.toWithBookingsDtoList(null);

        assertThat(getItemDtos, nullValue());
    }

    @Test
    void testToGetItemDtoWhenAllFieldsProvidedThenCorrectlyMapped() {
        Item item = new Item(1L, "Item1", "Description1", true, null, null);
        ShortBookingDto lastBooking = new ShortBookingDto(1L, 1L, null, null, null);
        ShortBookingDto nextBooking = new ShortBookingDto(2L, 2L, null, null, null);

        GetItemDto getItemDto = itemMapper.toGetItemDto(item, lastBooking, nextBooking);

        assertThat(getItemDto.getId(), is(item.getId()));
        assertThat(getItemDto.getName(), is(item.getName()));
        assertThat(getItemDto.getDescription(), is(item.getDescription()));
        assertThat(getItemDto.getAvailable(), is(item.getAvailable()));
        assertThat(getItemDto.getLastBooking(), is(lastBooking));
        assertThat(getItemDto.getNextBooking(), is(nextBooking));
    }

    @Test
    void testToGetItemDtoWhenLastBookingAndNextBookingNullThenCorrectlyMapped() {
        Item item = new Item(1L, "Item1", "Description1", true, null, null);

        GetItemDto getItemDto = itemMapper.toGetItemDto(item, null, null);

        assertThat(getItemDto.getId(), is(item.getId()));
        assertThat(getItemDto.getName(), is(item.getName()));
        assertThat(getItemDto.getDescription(), is(item.getDescription()));
        assertThat(getItemDto.getAvailable(), is(item.getAvailable()));
        assertThat(getItemDto.getLastBooking(), nullValue());
        assertThat(getItemDto.getNextBooking(), nullValue());
    }

}
