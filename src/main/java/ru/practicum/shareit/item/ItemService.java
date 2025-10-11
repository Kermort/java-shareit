package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentTextOnlyDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDataDto;

import java.util.List;

public interface ItemService {
    List<ItemFullDataDto> findAll(Long userId);

    ItemFullDataDto findById(Long id, Long userId);

    ItemDto create(ItemDto newItemDto, Long userId);

    ItemDto patch(Long itemId, ItemDto newItemDto, Long userId);

    List<ItemDto> search(String text);

    CommentDto createComment(CommentTextOnlyDto dto, Long itemId, Long userId);
}
