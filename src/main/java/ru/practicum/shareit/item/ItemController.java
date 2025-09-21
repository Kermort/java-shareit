package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 * TODO Вам нужно реализовать добавление новых вещей, их редактирование, просмотр списка вещей и поиск
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceImpl itemService;

    @GetMapping
    public List<ItemDto> findAll(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findAll(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@PathVariable Long itemId) {
        return itemService.findById(itemId);
    }

    @PostMapping
    public Item create(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.create(itemDto, userId);
    }

    @PutMapping
    public Item update(@Valid @RequestBody Item newItem) {
        return itemService.update(newItem);
    }

    @PatchMapping("/{itemId}")
    public Item patch(@PathVariable Long itemId,
                      @RequestBody ItemDto newItemDto,
                      @RequestHeader("X-Sharer-User-Id") Long userId
                      ) {
        return itemService.patch(itemId, newItemDto, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }
}
