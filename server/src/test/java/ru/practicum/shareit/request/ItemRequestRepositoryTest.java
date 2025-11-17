package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByRequesterId_whenFound_thenReturnListWithRequest() {
        User requester = entityManager.persistAndFlush(User.builder()
                .name("requester")
                .email("email@email.com")
                .build());
        ItemRequest ir = entityManager.persistAndFlush(ItemRequest.builder()
                        .description("description")
                        .requestor(requester)
                        .created(LocalDateTime.now())
                .build());

        List<ItemRequest> irFromBaseList = itemRequestRepository.findByRequestorId(requester.getId());

        assertFalse(irFromBaseList.isEmpty());
        assertEquals(ir.getDescription(), irFromBaseList.get(0).getDescription());
    }

    @Test
    void findByRequesterId_whenNotFound_thenReturnEmptyList() {
        List<ItemRequest> irFromBaseList = itemRequestRepository.findByRequestorId(Long.MAX_VALUE);

        assertTrue(irFromBaseList.isEmpty());
    }

    @Test
    void findAllByOtherUsers_whenFound_thenReturnListWithRequest() {
        User user1 = entityManager.persistAndFlush(User.builder()
                .name("user1")
                .email("user1@email.com")
                .build());
        User user2 = entityManager.persistAndFlush(User.builder()
                .name("user2")
                .email("user2@email.com")
                .build());
        ItemRequest ir2 = entityManager.persistAndFlush(ItemRequest.builder()
                .description("description2")
                .requestor(user2)
                .created(LocalDateTime.now())
                .build());

        List<ItemRequest> irFromBaseList = itemRequestRepository.findAllByOtherUsers(user1.getId());

        assertEquals(1, irFromBaseList.size());
        assertEquals(ir2.getDescription(), irFromBaseList.get(0).getDescription());
    }

    @Test
    void findAllByOtherUsers_whenNotFound_thenReturnEmptyList() {
        User user1 = entityManager.persistAndFlush(User.builder()
                .name("user1")
                .email("user1@email.com")
                .build());

        List<ItemRequest> irFromBaseList = itemRequestRepository.findAllByOtherUsers(user1.getId());

        assertTrue(irFromBaseList.isEmpty());
    }
}
