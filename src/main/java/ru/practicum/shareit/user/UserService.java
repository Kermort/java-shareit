package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto findById(Long id) {
        if (id == null) {
            throw new ValidationException("id не может быть пустым");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("пользователь с id " + id + " не найден."));
        return UserDtoMapper.toDto(user);
    }

    public UserDto create(UserDto newUserDto) {
        log.info("попытка создать пользователя c именем {} и почтой {}", newUserDto.getName(), newUserDto.getEmail());
        User newUser = UserDtoMapper.toModel(newUserDto);
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new DuplicateException("Выбранный email уже используется");
        }
        log.info("пользователь c именем {} и почтой {} создан", newUserDto.getName(), newUserDto.getEmail());
        return UserDtoMapper.toDto(userRepository.create(newUser));
    }

    public UserDto patch(Long userId, UserDto newUserDto) {
        log.info("Попытка частичного обновления пользователя с id {}", userId);
        User oldUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("не найден пользователь с id " + userId));

        if (newUserDto.getEmail() != null) {
            Optional<User> userByEmailOpt = userRepository.findByEmail(newUserDto.getEmail());
            if (userByEmailOpt.isPresent() && !userId.equals(userByEmailOpt.get().getId())) {
                throw new DuplicateException("Выбранный email недоступен");
            }
            oldUser.setEmail(newUserDto.getEmail());
        }
        if (newUserDto.getName() != null) {
            oldUser.setName(newUserDto.getName());
        }
        userRepository.update(oldUser);

        return UserDtoMapper.toDto(oldUser);
    }

    public int delete(Long userId) {
        if (userId == null) {
            throw new ValidationException("id не может быть пустым");
        }

        return userRepository.delete(userId);
    }
}
