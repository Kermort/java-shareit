package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    /**
     * получение всех пользователей
     */
    @Override
    public List<UserDto> findAll() {
        log.info("Получение всех пользователей");
        return userRepository.findAll().stream()
                .map(UserDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * получение пользователя по id
     */
    @Override
    public UserDto findById(Long id) {
        log.info("Получение пользователя по id {}", id);
        if (id == null) {
            throw new ValidationException("id не может быть пустым");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("пользователь с id " + id + " не найден."));
        return UserDtoMapper.toDto(user);
    }

    /**
     * создание пользователя
     */
    @Override
    @Transactional
    public UserDto create(UserDto newUserDto) {
        log.info("создание пользователя c именем {} и почтой {}", newUserDto.getName(), newUserDto.getEmail());
        User newUser = UserDtoMapper.toModel(newUserDto);
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new DuplicateException("Выбранный email уже используется");
        }
        log.info("пользователь c именем {} и почтой {} создан", newUserDto.getName(), newUserDto.getEmail());
        return UserDtoMapper.toDto(userRepository.save(newUser));
    }

    /**
     * изменение пользователя
     */
    @Override
    @Transactional
    public UserDto patch(Long userId, UserDto newUserDto) {
        log.info("частичное обновление пользователя с id {}", userId);
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
        userRepository.save(oldUser);

        return UserDtoMapper.toDto(oldUser);
    }

    /**
     * удаление пользователя по id
     */
    @Override
    @Transactional
    public void delete(Long userId) {
        if (userId == null) {
            throw new ValidationException("id не может быть пустым");
        }

        userRepository.deleteById(userId);
    }
}
