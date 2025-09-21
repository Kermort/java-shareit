package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        if (id == null) {
            throw new ValidationException("id не может быть пустым");
        }

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("пользователь с id " + id + " не найден.");
        }

        return userOpt.get();
    }

    public User create(User newUser) {
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new DuplicateException("Выбранный email уже используется");
        }
        return userRepository.create(newUser);
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("id не может быть пустым");
        }

        Optional<User> userOpt = userRepository.findById(newUser.getId());
        if (userOpt.isEmpty()) {
            throw new NotFoundException("пользователь с id = " + newUser.getId() + " не найден");
        }

        if (!userOpt.get().getEmail().equals(newUser.getEmail())
                && userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new ValidationException("Выбранный email уже используется");
        }

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(userOpt.get().getName());
        }

        return userRepository.update(newUser);
    }

    public User patch(Long userId, User newUser) {
        log.info("Попытка частичного обновления пользователя с id {}", userId);
        Optional<User> oldUserOpt = userRepository.findById(userId);
        if (oldUserOpt.isEmpty()) {
            throw new NotFoundException("не найден пользователь с id " + userId);
        }
        User oldUser = oldUserOpt.get();
        if (newUser.getEmail() != null) {
            Optional<User> userByEmailOpt = userRepository.findByEmail(newUser.getEmail());
            if (userByEmailOpt.isPresent()) {
                throw new DuplicateException("Выбранный email недоступен");
            }
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        return oldUser;
    }

    public int delete(Long userId) {
        if (userId == null) {
            throw new ValidationException("id не может быть пустым");
        }

        return userRepository.delete(userId);
    }
}
