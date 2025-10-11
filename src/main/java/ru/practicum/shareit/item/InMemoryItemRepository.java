package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> itemStorage;

    public List<Item> findAll(Long userId) {
        return itemStorage.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(itemStorage.get(id));
    }

    public Item create(Item newItem) {
        newItem.setId(getNextId());
        itemStorage.put(newItem.getId(), newItem);
        return newItem;
    }

    public Item update(Item newItem) {
        itemStorage.put(newItem.getId(), newItem);
        return newItem;
    }

    public Item patch(Item newItem) {
        itemStorage.put(newItem.getId(), newItem);
        return newItem;
    }

    public List<Item> search(String text) {
        return itemStorage.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    private long getNextId() {
        long currentMaxId = itemStorage.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
