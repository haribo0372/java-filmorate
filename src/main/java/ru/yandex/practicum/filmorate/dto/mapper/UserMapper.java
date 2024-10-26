package ru.yandex.practicum.filmorate.dto.mapper;

import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.User;

public class UserMapper {
    public static User fromNewUserRequestToUser(NewUserRequest request) {
        String name = request.getName();

        return new User(
                null,
                request.getEmail(),
                request.getLogin(),
                name == null || name.isBlank() ? request.getLogin() : name,
                request.getBirthday());
    }

    public static User fromUpdateUserRequestToUser(UpdateUserRequest request) {
        return new User(
                request.getId(),
                request.getEmail(),
                request.getLogin(),
                request.getName(),
                request.getBirthday());
    }

    public static UserDto fromUserToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setLogin(user.getLogin());
        dto.setBirthday(user.getBirthday());
        user.getFriends().forEach(dto::addFriend);
        return dto;
    }
}
