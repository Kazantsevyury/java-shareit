package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;
import org.springframework.data.repository.query.Param;


import java.util.Collection;
import java.util.List;

public interface CommentStorage extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.item i JOIN FETCH c.author u WHERE i.id = :itemId")
    List<Comment> findAllByItemId(@Param("itemId") Long itemId);

    @Query("SELECT c FROM Comment c JOIN FETCH c.item i JOIN FETCH c.author u WHERE i.id IN :itemIds")
    List<Comment> findAllByItemIdIn(@Param("itemIds") Collection<Long> itemIds);
}