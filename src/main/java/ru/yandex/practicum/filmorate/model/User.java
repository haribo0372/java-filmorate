package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotations.WithoutSpaces;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id;

    @Email(message = "Указанный email адрес имеет неверный формат")
    private String email;

    @NotBlank(message = "Логин не должен быть пустым")
    @WithoutSpaces(message = "Логин не должен содержать пробелы")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    private Set<Long> friends;

    public User(Long id, String email,
                String login, String name,
                LocalDate birthday, Set<Long> friends) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = friends == null ? new HashSet<>() : friends;
    }

    public User(Long id, String email,
                String login, String name,
                LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = new HashSet<>();
    }

    public void addFriend(Long id) {
        friends.add(id);
    }

    public void removeFromFriends(Long id) {
        friends.remove(id);
    }
}
