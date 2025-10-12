package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDescriptionOnlyDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDescriptionOnlyDto dto, Long userId);

    List<ItemRequestWithItemsDto> findByRequestorId(Long userId);

    List<ItemRequestDto> findAllByOtherUsers(Long userId);

    ItemRequestWithItemsDto findById(Long requestId);
}
