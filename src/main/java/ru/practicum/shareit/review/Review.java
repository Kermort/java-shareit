package ru.practicum.shareit.review;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Data
public class Review {
    private Item item;
    private User user;
    private String review;
}
