package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemForRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestDescriptionOnlyDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestDto create(ItemRequestDescriptionOnlyDto dto, Long userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь с id " + userId + " не найден"));

        ItemRequest request = ItemRequestDtoMapper.toModel(dto, requestor, LocalDateTime.now());
        return ItemRequestDtoMapper.toDto(itemRequestRepository.save(request));
    }

    public List<ItemRequestWithItemsDto> findByRequestorId(Long userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь с id " + userId + " не найден"));
        List<ItemRequest> requests = itemRequestRepository.findByRequestorId(userId);
        if (requests.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> requestIds = requests.stream().map(ItemRequest::getId).toList();
        List<ItemForRequestInfoDto> items = itemRepository.findByRequestIds(requestIds).stream()
                .map(ItemDtoMapper::toRequestInfoDto).toList();

        return requests.stream()
                .map(r -> ItemRequestDtoMapper.toInfoWithItemsDto(r,
                        items.stream().filter(i -> i.getRequestId().equals(r.getId())).toList()))
                .toList();
    }

    public List<ItemRequestDto> findAllByOtherUsers(Long userId) {
        List<ItemRequest> requests = itemRequestRepository.findAllByOtherUsers(userId);
        return requests.stream().map(ItemRequestDtoMapper::toDto).toList();
    }

    public ItemRequestWithItemsDto findById(Long requestId) {
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("запрос с id " + requestId + " не найден"));
        List<ItemForRequestInfoDto> items = itemRepository.findByRequestIds(List.of(requestId)).stream()
                .map(ItemDtoMapper::toRequestInfoDto).toList();

        return ItemRequestDtoMapper.toInfoWithItemsDto(request, items);
    }
}
