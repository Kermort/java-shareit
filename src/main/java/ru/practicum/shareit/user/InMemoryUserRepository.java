package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> userStorage = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(userStorage.get(userId));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userStorage.values().stream().filter(u -> u.getEmail().equals(email)).findFirst();
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        userStorage.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public int delete(Long userId) {
        return userStorage.remove(userId) != null ? 1 : 0;
    }

    private long getNextId() {
        long currentMaxId = userStorage.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
