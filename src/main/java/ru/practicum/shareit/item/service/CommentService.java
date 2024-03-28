package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.comment.CommentCreateDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public interface CommentService {

    Comment save(Comment comment);
}