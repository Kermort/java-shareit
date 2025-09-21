package ru.practicum.shareit.user;

import lombok.Data;
import jakarta.validation.constraints.*;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {
    private Long id;
    private String name;
    @Email
    @NotBlank
    private String email;
}
