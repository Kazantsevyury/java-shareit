package ru.practicum.shareit.Item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.comment.AddCommentDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommentMapperTest {

    private CommentMapper mapper;

    @BeforeEach
    public void setup() {
        mapper = Mappers.getMapper(CommentMapper.class);
    }

    @Test
    public void toCommentDto_ShouldMapCorrectly() {
        Comment comment = new Comment(1L, "Great!", new Item(), new User(), LocalDateTime.now());
        CommentDto result = mapper.toCommentDto(comment);

        assertNotNull(result);
        assertEquals(comment.getText(), result.getText());
        // Assuming author's name is set in the User object of Comment
        // You might need to adjust this depending on how the User object is populated
    }

    @Test
    public void fromCommentCreateDto_ShouldMapCorrectly() {
        AddCommentDto dto = new AddCommentDto("Awesome!");
        Comment result = mapper.fromCommentCreateDto(dto);

        assertNotNull(result);
        assertEquals(dto.getText(), result.getText());
        assertNotNull(result.getCreated()); // Verify the creation date is set
    }

    @Test
    public void toDtoList_ShouldMapCorrectly() {
        Comment comment = new Comment(1L, "Great!", new Item(), new User(), LocalDateTime.now());
        List<CommentDto> result = mapper.toDtoList(Collections.singletonList(comment));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(comment.getText(), result.get(0).getText());
    }
}