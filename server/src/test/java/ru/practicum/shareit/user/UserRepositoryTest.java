package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByEmail_whenUserFound_thenReturnUser() {
        User user = entityManager.persistAndFlush(User.builder()
                .name("user")
                .email("email@email.com")
                .build());

        Optional<User> userFromBaseOpt = userRepository.findByEmail("email@email.com");

        assertTrue(userFromBaseOpt.isPresent());
        assertEquals(user.getEmail(), userFromBaseOpt.get().getEmail());
    }

    @Test
    void findByEmail_whenUserNotFound_thenReturnEmptyOptional() {
        Optional<User> userFromBaseOpt = userRepository.findByEmail("email@email.com");

        assertTrue(userFromBaseOpt.isEmpty());
    }
}
