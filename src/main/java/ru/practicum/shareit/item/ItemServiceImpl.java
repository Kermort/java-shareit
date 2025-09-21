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
import java.util.Objects;
import java.util.Optional;
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

        Optional<Item> itemOpt = itemRepository.findById(id);
        if (itemOpt.isEmpty()) {
            throw new NotFoundException("вешь с id " + id + " не найдена.");
        }

        return ItemDtoMapper.toDto(itemOpt.get());
    }

    public Item create(ItemDto newItemDto, Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("пользователь с id " + userId + "не найден");
        }

        Item newItem = ItemDtoMapper.fromDto(newItemDto, userId);
        newItem.setOwner(userOpt.get());

        return itemRepository.create(newItem);
    }

    public Item update(Item newItem) {
        if (newItem.getId() == null) {
            throw new ValidationException("id не может быть пустым");
        }

        Optional<Item> itemOpt = itemRepository.findById(newItem.getId());
        if (itemOpt.isEmpty()) {
            throw new NotFoundException("вешь с id = " + newItem.getId() + " не найдена");
        }

        if (newItem.getName() == null || newItem.getName().isBlank()) {
            newItem.setName(itemOpt.get().getName());
        }

        if (newItem.getDescription() == null || newItem.getDescription().isBlank()) {
            newItem.setDescription(itemOpt.get().getDescription());
        }

        return itemRepository.update(newItem);
    }

    public Item patch(Long itemId, ItemDto newItemDto, Long userId) {
        log.info("Попытка частичного обновления вещи с id {}", itemId);
        Optional<Item> oldItemOpt = itemRepository.findById(itemId);
        if (oldItemOpt.isEmpty()) {
            throw new NotFoundException("не найдена вещь с id " + itemId);
        }
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("пользователь с id " + userId + " не найден");
        }

        if (!Objects.equals(userOpt.get().getId(), userId)) {
            throw new NotAuthorizedException("пользователь с id " + userId + " не является владельцем вещи с id " + itemId);
        }
        Item newItem = ItemDtoMapper.fromDto(newItemDto, userId);
        Item oldItem = oldItemOpt.get();

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
        return oldItem;
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