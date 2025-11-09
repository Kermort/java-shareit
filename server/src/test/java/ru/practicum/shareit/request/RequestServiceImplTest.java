package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDescriptionOnlyDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@Import({ItemRequestServiceImpl.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RequestServiceImplTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User requester;

    @BeforeEach
    void init() {
        requester = User.builder()
                .id(1L)
                .name("Requester Name")
                .email("email@email.com")
                .build();


    }

    @Test
    void create_whenSuccess_thenReturnItemRequestDto() {
        ItemRequestDescriptionOnlyDto dto = new ItemRequestDescriptionOnlyDto();
        dto.setDescription("description");
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(requester)
                .created(LocalDateTime.now())
                .build();
        when(itemRequestRepository.save(any(ItemRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemRequestDto result = itemRequestService.create(dto, 1L);

        assertEquals(itemRequest.getDescription(), result.getDescription());
    }

    @Test
    void create_whenUserNotFound_thenThrowException() {
        ItemRequestDescriptionOnlyDto dto = new ItemRequestDescriptionOnlyDto();
        dto.setDescription("description");
        when(userRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.create(dto, Long.MAX_VALUE));
    }

    @Test
    void findByRequestorId_whenFound_thenReturnItemRequestList() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(requester)
                .created(LocalDateTime.now())
                .build();
        when(itemRequestRepository.findByRequestorId(1L)).thenReturn(List.of(itemRequest));
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(itemRepository.findByRequestIds(anyList())).thenReturn(List.of());

        List<ItemRequestWithItemsDto> result = itemRequestService.findByRequestorId(requester.getId());

        assertFalse(result.isEmpty());
        assertEquals(itemRequest.getId(), result.get(0).getId());
    }

    @Test
    void findByRequestorId_whenUserNotFound_thenThrowException() {
        when(userRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.findByRequestorId(Long.MAX_VALUE));
    }

    @Test
    void findAllByOtherUsers_whenFound_thenReturnItemRequestList() {
        User otherUser = User.builder()
                .id(2L)
                .name("user Name")
                .email("useremail@email.com")
                .build();
        ItemRequest ir = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(otherUser)
                .created(LocalDateTime.now())
                .build();
        when(itemRequestRepository.findAllByOtherUsers(requester.getId())).thenReturn(List.of(ir));

        List<ItemRequestDto> result = itemRequestService.findAllByOtherUsers(requester.getId());

        assertEquals(1, result.size());
        assertEquals(ir.getId(), result.get(0).getId());
    }

    @Test
    void findById_whenFound_thenReturnItemRequestDto() {
        ItemRequest ir = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(requester)
                .created(LocalDateTime.now())
                .build();
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(ir));

        ItemRequestWithItemsDto result = itemRequestService.findById(1L);

        assertNotNull(result);
        assertEquals(ir.getId(), result.getId());
    }

    @Test
    void findById_whenNotFound_thenThrowException() {
        when(itemRequestRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.findById(Long.MAX_VALUE));
    }
}
