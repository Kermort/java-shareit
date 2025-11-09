package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import({UserServiceImpl.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void init() {
        userDto = UserDto.builder()
                .name("user")
                .email("email@email.com")
                .build();
        user = User.builder()
                .id(1L)
                .name("user")
                .email("email@email.com")
                .build();
    }

    @Test
    void findAll_whenFound_thenReturnUserDtoList() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> userDtoList = userService.findAll();

        assertEquals(1, userDtoList.size());
        assertEquals(user.getName(), userDtoList.get(0).getName());
    }

    @Test
    void findAll_whenNotFound_thenReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> userDtoList = userService.findAll();

        assertTrue(userDtoList.isEmpty());
    }

    @Test
    void findById_whenFound_thenReturnUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto foundUser = userService.findById(1L);

        assertNotNull(foundUser);
        assertEquals(user.getName(), foundUser.getName());
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    void findById_whenNotFound_thenThrowException() {
        when(userRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(Long.MAX_VALUE));
    }

    @Test
    void findById_whenIdIsNull_thenThrowException() {
        assertThrows(ValidationException.class, () -> userService.findById(null));
    }

    @Test
    void create_whenSuccess_thenReturnUserDto() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        UserDto result = userService.create(userDto);

        assertEquals(userDto.getName(), result.getName());
    }

    @Test
    void create_whenEmailIsDuplicate_thenThrowException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(DuplicateException.class, () -> userService.create(userDto));
    }

    @Test
    void patch_whenSuccess_thenReturnUserDto() {
        UserDto updatedUserDto = UserDto.builder()
                .name("updated user")
                .email("email@email.com")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userService.patch(1L, updatedUserDto);

        assertEquals(updatedUserDto.getName(), result.getName());
    }

    @Test
    void patch_whenUserNotFound_thenThrowException() {
        when(userRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(Long.MAX_VALUE));
    }

    @Test
    void patch_whenEmailIsUnavailable_thenThrowException() {
        UserDto updatedUserDto = UserDto.builder()
                .name("updated user")
                .email("otheremail@email.com")
                .build();
        User otherUser = User.builder()
                .id(2L)
                .name("other user")
                .email("otheremail@email.com")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(otherUser.getEmail())).thenReturn(Optional.of(otherUser));

        assertThrows(DuplicateException.class, () -> userService.patch(1L, updatedUserDto));
    }

    @Test
    void delete_whenIdIsNotNull_thenInvokeDelete() {
        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        userService.delete(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void delete_whenIdIsNull_thenThrowException() {
        assertThrows(ValidationException.class, () -> userService.delete(null));
    }
}
