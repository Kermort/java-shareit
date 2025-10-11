package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> findAll(Long userId);

    ItemDto findById(Long id);

    ItemDto create(ItemDto newItemDto, Long userId);

    ItemDto patch(Long itemId, ItemDto newItemDto, Long userId);

    List<ItemDto> search(String text);
}
