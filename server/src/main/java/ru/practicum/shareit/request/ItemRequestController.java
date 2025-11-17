package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDescriptionOnlyDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestDescriptionOnlyDto dto,
                                 @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.create(dto, userId);
    }

    @GetMapping
    public List<ItemRequestWithItemsDto> findByRequestorId(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.findByRequestorId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllByOtherUsers(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.findAllByOtherUsers(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto findById(@PathVariable("requestId") Long requestId) {
        return itemRequestService.findById(requestId);
    }
}
