package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotAuthorizedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public List<ItemDto> findAll(Long userId) {
        return itemRepository.findAll(userId).stream()
                .map(ItemDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public ItemDto findById(Long id) {
        if (id == null) {
            throw new ValidationException("id не может быть пустым");
        }

        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("вешь с id " + id + " не найдена."));
        return ItemDtoMapper.toDto(item);
    }

    public ItemDto create(ItemDto newItemDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("пользователь с id " + userId + "не найден"));
        Item newItem = ItemDtoMapper.toModel(newItemDto);
        newItem.setOwner(user);

        return ItemDtoMapper.toDto(itemRepository.create(newItem));
    }

    public ItemDto patch(Long itemId, ItemDto newItemDto, Long userId) {
        log.info("Попытка частичного обновления вещи с id {}", itemId);
        Item oldItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("не найдена вещь с id " + itemId));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("пользователь с id " + userId + " не найден"));

        if (!user.getId().equals(userId)) {
            throw new NotAuthorizedException("пользователь с id " + userId + " не является владельцем вещи с id " + itemId);
        }
        Item newItem = ItemDtoMapper.toModel(newItemDto);

        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }

        itemRepository.patch(oldItem);
        return ItemDtoMapper.toDto(oldItem);
    }

    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text).stream()
                .map(ItemDtoMapper::toDto)
                .collect(Collectors.toList());
    }
}