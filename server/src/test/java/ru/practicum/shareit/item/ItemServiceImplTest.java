package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingForItemInfoDto;
import ru.practicum.shareit.exception.NotAuthorizedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentTextOnlyDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDataDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@Import({ItemServiceImpl.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceImplTest {

    private User owner;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    void init() {
        owner = User.builder()
                .id(1L)
                .name("owner")
                .email("email@email.com")
                .build();
    }

    @Test
    void findAll_whenFound_thenReturnItemDtoWithBookingAndComment() {
        User author = User.builder()
                .id(2L)
                .name("author")
                .email("author@email.com")
                .build();
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@email.com")
                .build();
        Item item1 = Item.builder()
                .id(1L)
                .owner(owner)
                .name("item1")
                .description("description1")
                .available(true)
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .owner(owner)
                .name("item2")
                .description("description2")
                .available(true)
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .text("comment text")
                .author(author)
                .item(item1)
                .created(LocalDateTime.now())
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .item(item2)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .build();
        when(itemRepository.findByOwnerId(owner.getId())).thenReturn(List.of(item1, item2));
        when(bookingRepository.findLastBookingsByItemIdsAndStatus(
                anyList(),
                any(LocalDateTime.class),
                any(BookingStatus.class)))
                .thenReturn(List.of());
        when(bookingRepository.findNextBookingsByItemIdsAndStatus(
                anyList(),
                any(LocalDateTime.class),
                any(BookingStatus.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.findAllByItemIds(anyList())).thenReturn(List.of(comment));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        List<ItemFullDataDto> result = itemService.findAll(owner.getId());
        List<CommentDto> resultComments = result.get(0).getComments();
        BookingForItemInfoDto resultBooking = result.get(1).getNextBooking();

        assertEquals(2, result.size());
        assertEquals(Math.min(item1.getId(), item2.getId()),
                Math.min(result.get(0).getId(), result.get(1).getId()));
        assertEquals(Math.max(item1.getId(), item2.getId()),
                Math.max(result.get(0).getId(), result.get(1).getId()));
        assertEquals(1, resultComments.size());
        assertNotNull(resultBooking);
        assertEquals(booking.getId(), resultBooking.getId());
    }

    @Test
    void findAll_whenUserHasNoItems_thenReturnEmptyList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        List<ItemFullDataDto> result = itemService.findAll(owner.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_whenUserNotFound_thenThrowException() {
        when(userRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findAll(Long.MAX_VALUE));
    }

    @Test
    void findById_whenFoundAndUserIsOwner_thenReturnItemFullDataDto() {
        User author = User.builder()
                .id(2L)
                .name("author")
                .email("author@email.com")
                .build();
        User booker = User.builder()
                .id(3L)
                .name("booker")
                .email("booker@email.com")
                .build();
        Item item1 = Item.builder()
                .id(1L)
                .owner(owner)
                .name("item1")
                .description("description1")
                .available(true)
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .text("comment text")
                .author(author)
                .item(item1)
                .created(LocalDateTime.now())
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .item(item1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .build();
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
        when(bookingRepository.findLastBookingsByItemIdsAndStatus(
                anyList(),
                any(LocalDateTime.class),
                any(BookingStatus.class)))
                .thenReturn(List.of());
        when(bookingRepository.findNextBookingsByItemIdsAndStatus(
                anyList(),
                any(LocalDateTime.class),
                any(BookingStatus.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.findAllByItemIds(anyList())).thenReturn(List.of(comment));

        ItemFullDataDto result = itemService.findById(item1.getId(), owner.getId());
        List<CommentDto> resultComments = result.getComments();
        BookingForItemInfoDto resultBooking = result.getNextBooking();

        assertNotNull(result);
        assertEquals(item1.getId(), result.getId());
        assertEquals(1, resultComments.size());
        assertNotNull(resultBooking);
        assertEquals(booking.getId(), resultBooking.getId());
    }

    @Test
    void findById_whenFoundAndUserIsNotOwner_thenReturnItemFullDataDtoWithoutBookings() {
        Item item1 = Item.builder()
                .id(1L)
                .owner(owner)
                .name("item1")
                .description("description1")
                .available(true)
                .build();

        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        ItemFullDataDto result = itemService.findById(item1.getId(), Long.MAX_VALUE);
        BookingForItemInfoDto resultBooking = result.getNextBooking();

        assertNotNull(result);
        assertEquals(item1.getId(), result.getId());
        assertNull(resultBooking);
    }

    @Test
    void findById_whenItemNotFound_thenThrowException() {
        when(itemRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findById(Long.MAX_VALUE, 1L));
    }

    @Test
    void create_whenUserNotFound_thenThrowException() {
        ItemDto newItemDto = ItemDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .build();
        when(userRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(newItemDto, Long.MAX_VALUE));
    }

    @Test
    void create_whenNotByRequestAndSuccess_thenReturnItemDtoWithoutRequest() {
        Item item = Item.builder()
                .id(1L)
                .owner(owner)
                .name("item")
                .description("description")
                .available(true)
                .build();
        ItemDto newItemDto = ItemDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .build();
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto result = itemService.create(newItemDto, owner.getId());

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        assertNull(result.getRequestId());
    }

    @Test
    void create_whenByRequestAndSuccess_thenReturnItemDtoWithRequestId() {
        User requester = User.builder()
                .id(2L)
                .name("requester")
                .email("requester@email.com")
                .build();
        ItemRequest ir = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(requester)
                .created(LocalDateTime.now())
                .build();
        Item item = Item.builder()
                .id(1L)
                .owner(owner)
                .name("item")
                .description("description")
                .available(true)
                .request(ir)
                .build();
        ItemDto newItemDto = ItemDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .requestId(ir.getId())
                .build();
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(itemRequestRepository.findById(newItemDto.getRequestId())).thenReturn(Optional.of(ir));

        ItemDto result = itemService.create(newItemDto, owner.getId());

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        assertNotNull(result.getRequestId());
        assertEquals(ir.getId(), result.getRequestId());
    }

    @Test
    void patch_whenSuccess_thenReturnItemDto() {
        Item item = Item.builder()
                .id(1L)
                .owner(owner)
                .name("item")
                .description("description")
                .available(true)
                .build();
        ItemDto newItemDto = ItemDto.builder()
                .name("patched item")
                .description("patched description")
                .build();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto result = itemService.patch(item.getId(), newItemDto, owner.getId());

        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
    }

    @Test
    void patch_whenItemNotFound_thenThrowException() {
        ItemDto newItemDto = ItemDto.builder()
                .name("patched item")
                .description("patched description")
                .build();
        when(itemRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());
        //when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        assertThrows(NotFoundException.class, () -> itemService.patch(Long.MAX_VALUE, newItemDto, owner.getId()));
    }

    @Test
    void patch_whenUserNotFound_thenThrowException() {
        Item item = Item.builder()
                .id(1L)
                .owner(owner)
                .name("item")
                .description("description")
                .available(true)
                .build();
        ItemDto newItemDto = ItemDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .build();
        when(userRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemService.patch(item.getId(), newItemDto, Long.MAX_VALUE));
    }

    @Test
    void patch_whenUserIsNotOwner_thenThrowException() {
        Item item = Item.builder()
                .id(1L)
                .owner(owner)
                .name("item")
                .description("description")
                .available(true)
                .build();
        ItemDto newItemDto = ItemDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .build();
        User otherUser = User.builder()
                .id(2L)
                .name("other user")
                .email("other@email.com")
                .build();

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));

        assertThrows(NotAuthorizedException.class, () -> itemService.patch(item.getId(), newItemDto, otherUser.getId()));
    }

    @Test
    void search_whenFound_thenReturnItemDtoList() {
        Item item = Item.builder()
                .id(1L)
                .owner(owner)
                .name("item")
                .description("description")
                .available(true)
                .build();
        when(itemRepository.search("item")).thenReturn(List.of(item));

        List<ItemDto> result = itemService.search("item");

        assertFalse(result.isEmpty());
        assertEquals(item.getId(), result.get(0).getId());
    }

    @Test
    void search_whenNotFound_thenReturnEmptyList() {
        when(itemRepository.search("text")).thenReturn(List.of());

        List<ItemDto> result = itemService.search("text");

        assertTrue(result.isEmpty());
    }

    @Test
    void search_whenItemIsNotAvailable_thenReturnEmptyList() {
        when(itemRepository.search("item")).thenReturn(List.of());

        List<ItemDto> result = itemService.search("item");

        assertTrue(result.isEmpty());
    }

    @Test
    void createComment() {
        Item item = Item.builder()
                .id(1L)
                .owner(owner)
                .name("item")
                .description("description")
                .available(false)
                .build();
        User booker = User.builder()
                .id(2L)
                .name("booker")
                .email("booker@email.com")
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .build();
        CommentTextOnlyDto comment = new CommentTextOnlyDto();
        comment.setText("comment");
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingRepository.findPastBookingsByBookerIdAndStatus(
                    eq(booker.getId()),
                    any(LocalDateTime.class),
                    eq(BookingStatus.APPROVED)))
                .thenReturn(List.of(booking));

        CommentDto result = itemService.createComment(comment, item.getId(), booker.getId());

        assertNotNull(result);
        assertEquals(comment.getText(), result.getText());
    }
}
