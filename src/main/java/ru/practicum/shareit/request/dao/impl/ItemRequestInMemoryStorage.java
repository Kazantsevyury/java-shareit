package ru.practicum.shareit.request.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ItemRequestInMemoryStorage implements ItemRequestStorage {
    private final Map<Long, ItemRequest> requests = new HashMap<>();
    private Long currentId = Long.valueOf(0);

    @Override
    public ItemRequest add(ItemRequest itemRequest) {
        itemRequest.setId(++currentId);
        requests.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }

    @Override
    public void remove(Long id) {
        requests.remove(id);
    }

    @Override
    public void update(ItemRequest itemRequest) {
        if (requests.containsKey(itemRequest.getId())) {
            requests.put(itemRequest.getId(), itemRequest);
        }
    }

    @Override
    public Collection<ItemRequest> findAll() {
        return requests.values();
    }

    @Override
    public ItemRequest findById(Long id) {
        ItemRequest itemRequest = requests.get(id);
        if (itemRequest == null) {

            throw new IllegalArgumentException("ItemRequest с id " + id + " не найдено");
        }
        return itemRequest;
    }
}
