package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDto> findAll(Long userId);

    ItemDto findById(Long id);

    Item create(ItemDto newItemDto, Long userId);

    Item update(Item newItem);

    Item patch(Long itemId, ItemDto newItemDto, Long userId);

    List<ItemDto> search(String text);
}
