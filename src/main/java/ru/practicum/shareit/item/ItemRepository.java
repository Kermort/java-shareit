package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> findAll(Long userId);

    Optional<Item> findById(Long id);

    Item create(Item item);

    Item update(Item newItem);

    Item patch(Item newItem);

    List<Item> search(String text);
}
