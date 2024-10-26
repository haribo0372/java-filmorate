package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.mapper.UserMapper;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public UserDto addToFriends(Long userId1, Long userId2) {
        User user1 = userStorage.findById(userId1);
        userStorage.findById(userId2);

        if (userStorage.addFriendship(userId1, userId2))
            log.info("Пользователь с id={} отправил запрос на добавление " +
                    "в друзья пользователю с id={}", userId1, userId2);

        user1.setFriends(userStorage.findAllFriendsIdByUserId(userId1));
        return UserMapper.fromUserToUserDto(user1);
    }

    public UserDto addUser(NewUserRequest request) {
        User user = UserMapper.fromNewUserRequestToUser(request);
        User savedUser = userStorage.save(user);
        log.info("Пользователь с id={} сохранен", savedUser.getId());
        return UserMapper.fromUserToUserDto(savedUser);
    }

    public UserDto updateUser(UpdateUserRequest request) {
        User user = UserMapper.fromUpdateUserRequestToUser(request);
        User storageUser = userStorage.findById(user.getId());

        if (user.getName() != null) storageUser.setName(user.getName());
        if (user.getLogin() != null) storageUser.setLogin(user.getLogin());
        if (user.getEmail() != null) storageUser.setEmail(user.getEmail());
        if (user.getBirthday() != null) storageUser.setBirthday(user.getBirthday());

        User updatedUser = userStorage.update(storageUser);
        log.info("Пользователь с id={} обновлен", updatedUser.getId());

        return UserMapper.fromUserToUserDto(updatedUser);
    }

    public Collection<UserDto> findAll() {
        return userStorage.findAll().stream()
                .map(UserMapper::fromUserToUserDto).toList();
    }

    public UserDto findById(Long id) {
        return UserMapper.fromUserToUserDto(userStorage.findById(id));
    }

    public UserDto removeFromFriends(Long userId1, Long userId2) {
        User user1 = userStorage.findById(userId1);
        userStorage.findById(userId2);

        userStorage.removeFriendship(userId1, userId2);
        log.info("Пользователь с id={} удалил пользователя с id={} из списка друзей", userId1, userId2);
        return UserMapper.fromUserToUserDto(user1);
    }

    public Collection<UserDto> getCommonFriends(Long userId1, Long userId2) {
        return userStorage.getCommonFriends(userId1, userId2).stream()
                .map(UserMapper::fromUserToUserDto).toList();
    }

    public Collection<UserDto> getFriends(Long userId) {
        userStorage.findById(userId);
        return userStorage.getAllFriendsByUserId(userId).stream()
                .map(UserMapper::fromUserToUserDto).toList();
    }
}
