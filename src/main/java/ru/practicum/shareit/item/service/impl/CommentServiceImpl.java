package ru.practicum.shareit.item.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.storage.CommentStorage;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentStorage commentStorage;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public Comment save(Comment comment) {
        Comment savedComment = commentStorage.save(comment);
        return savedComment;
    }

    @Override
    public  List<Comment> findAllByItemId(Long itemID) {
        return commentStorage.findAllByItemId(itemID);
    }
}