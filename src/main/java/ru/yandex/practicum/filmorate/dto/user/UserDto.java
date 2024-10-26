package ru.yandex.practicum.filmorate.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Long> friends;

    public UserDto(Long id, String email, String login, String name, LocalDate birthday, Set<Long> friends) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = friends == null ? new HashSet<>() : friends;
    }

    public UserDto(Long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = new HashSet<>();
    }

    public UserDto() {
    }

    public void addFriend(Long friend) {
        friends.add(friend);
    }
}
