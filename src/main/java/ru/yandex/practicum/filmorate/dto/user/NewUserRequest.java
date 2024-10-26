package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.annotations.WithoutSpaces;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @Email(message = "Указанный email адрес имеет неверный формат")
    private String email;

    @NotBlank(message = "Логин не должен быть пустым")
    @WithoutSpaces(message = "Логин не должен содержать пробелы")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}