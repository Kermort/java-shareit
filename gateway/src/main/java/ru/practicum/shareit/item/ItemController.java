package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentTextOnlyDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findAll(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all items, userId={}", userId);
        return itemClient.findAll(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@PathVariable Long itemId,
                                           @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get item by id {}, userId={}", itemId, userId);
        return itemClient.findById(itemId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                                         @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Create item {}, userId={}", itemDto, userId);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patch(@PathVariable Long itemId,
                                        @RequestBody ItemDto newItemDto,
                                        @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Patch item {}, userId={}", itemId, userId);
        return itemClient.patch(itemId, userId, newItemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        log.info("Search text");
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> create(@RequestBody CommentTextOnlyDto commentDto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable("itemId") Long itemId) {
        log.info("Create comment for item {}, userId={}", itemId, userId);
        return itemClient.createComment(itemId, userId, commentDto);
    }
}
