package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto findById(Long id);

    UserDto create(UserDto newUserDto);

    UserDto patch(Long userId, UserDto newUserDto);

    void delete(Long userId);




}
