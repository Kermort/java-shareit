package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllByItemIds_whenFound_thenReturnCommentsList() {
        User owner = entityManager.persistAndFlush(User.builder()
                .name("owner")
                .email("owner@email.com")
                .build());
        Item item = entityManager.persistAndFlush(Item.builder()
                .owner(owner)
                .name("item")
                .description("description")
                .available(true)
                .build());
        User author = entityManager.persistAndFlush(User.builder()
                .name("commenter")
                .email("commenter@email.com")
                .build());
        Comment comment = entityManager.persistAndFlush(Comment.builder()
                .text("text")
                .author(author)
                .item(item)
                .created(LocalDateTime.now())
                .build());

        List<Comment> commentsFromBase = commentRepository.findAllByItemIds(List.of(item.getId()));

        assertFalse(commentsFromBase.isEmpty());
        assertEquals(comment.getId(), commentsFromBase.get(0).getId());
    }

    @Test
    void findAllByItemIds_whenNotFound_thenReturnEmptyList() {
        List<Comment> commentsFromBase = commentRepository.findAllByItemIds(List.of(Long.MAX_VALUE));

        assertTrue(commentsFromBase.isEmpty());
    }
}
