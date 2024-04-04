package ru.practicum.shareit.item.dto;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    public void testSerialize() throws Exception {
        ItemDto dto = new ItemDto(1L, "Mjolnir", "Hammer of Thor", true, null);
        assertThat(json.write(dto)).hasJsonPathStringValue("@.name");
        assertThat(json.write(dto)).extractingJsonPathStringValue("@.name").isEqualTo("Mjolnir");
    }

    @Test
    public void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"name\":\"Mjolnir\",\"description\":\"Hammer of Thor\",\"available\":true}";
        assertThat(json.parse(content)).isEqualTo(new ItemDto(1L, "Mjolnir", "Hammer of Thor", true, null));
    }
}