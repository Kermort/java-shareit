package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDescriptionOnlyDto;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody ItemRequestDescriptionOnlyDto dto,
                                         @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Create item request, userId={}", userId);
        return itemRequestClient.create(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> findByRequestorId(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get requests, userId={}", userId);
        return itemRequestClient.findByRequestorId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllFromOtherUsers(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get request from other users, userId={}", userId);
        return itemRequestClient.findAllFromOtherUsers(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@PathVariable("requestId") Long requestId) {
        log.info("Get request {}", requestId);
        return itemRequestClient.findById(requestId);
    }
}
