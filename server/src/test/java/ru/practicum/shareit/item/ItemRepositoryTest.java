package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void findByOwnerId_whenItemFound_thenReturnListWithItem() {
        User owner = entityManager.persistAndFlush(User.builder()
                .name("user")
                .email("email@email.com")
                .build());
        Item item = entityManager.persistAndFlush(Item.builder()
                .owner(owner)
                .name("item")
                .description("description")
                .available(true)
                .build());

        List<Item> itemsFromBase = itemRepository.findByOwnerId(owner.getId());

        assertFalse(itemsFromBase.isEmpty());
        assertEquals(item.getName(), itemsFromBase.get(0).getName());
    }

    @Test
    void findByOwnerId_whenItemNotFound_thenReturnEmptyList() {
        List<Item> itemsFromBase = itemRepository.findByOwnerId(Long.MAX_VALUE);

        assertTrue(itemsFromBase.isEmpty());
    }

    @Test
    void search_whenFoundByName_thenReturnListWithItem() {
        User owner = entityManager.persistAndFlush(User.builder()
                .name("user")
                .email("email@email.com")
                .build());
        Item item = entityManager.persistAndFlush(Item.builder()
                .owner(owner)
                .name("item test name")
                .description("item test description")
                .available(true)
                .build());

        List<Item> itemsFromBase = itemRepository.search("name");

        assertFalse(itemsFromBase.isEmpty());
        assertEquals(item.getName(), itemsFromBase.get(0).getName());
    }

    @Test
    void search_whenFoundByDescription_thenReturnListWithItem() {
        User owner = entityManager.persistAndFlush(User.builder()
                .name("user")
                .email("email@email.com")
                .build());
        Item item = entityManager.persistAndFlush(Item.builder()
                .owner(owner)
                .name("item test name")
                .description("item test description")
                .available(true)
                .build());

        List<Item> itemsFromBase = itemRepository.search("description");

        assertFalse(itemsFromBase.isEmpty());
        assertEquals(item.getName(), itemsFromBase.get(0).getName());
    }

    @Test
    void search_whenNotFound_thenReturnEmptyList() {
        List<Item> itemsFromBase = itemRepository.search("qwerty");

        assertTrue(itemsFromBase.isEmpty());
    }

    @Test
    void findByRequestIds_whenFound_thenReturnNotEmptyList() {
        User owner = entityManager.persistAndFlush(User.builder()
                .name("owner")
                .email("owner@email.com")
                .build());
        User requester = entityManager.persistAndFlush(User.builder()
                .name("requester")
                .email("requester@email.com")
                .build());
        ItemRequest itemRequest = entityManager.persistAndFlush(ItemRequest.builder()
                .description("description")
                .requestor(requester)
                .created(LocalDateTime.now())
                .build());
        Item item = entityManager.persistAndFlush(Item.builder()
                .owner(owner)
                .name("item test name")
                .description("item test description")
                .available(true)
                .request(itemRequest)
                .build());

        List<Item> itemsFromBase = itemRepository.findByRequestIds(List.of(itemRequest.getId()));

        assertFalse(itemsFromBase.isEmpty());
        assertEquals(item.getName(), itemsFromBase.get(0).getName());
    }

    @Test
    void findByRequestIds_whenNotFound_thenReturnEmptyList() {
        List<Item> itemsFromBase = itemRepository.findByRequestIds(List.of(Long.MAX_VALUE));

        assertTrue(itemsFromBase.isEmpty());
    }

}
