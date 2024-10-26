package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
//@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long serialId = 0L;


    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findById(Long id) {
        User user = users.get(id);
        if (user == null)
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", id));

        return user;
    }

    @Override
    public User remove(Long id) {
        if (users.get(id) == null)
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", id));

        return users.remove(id);
    }

    @Override
    public User save(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя для отображения пустое, поле name заполняется логином");
            user.setName(user.getLogin());
        }

        user.setId(++serialId);
        users.put(user.getId(), user);
        log.info("Пользователь с id={} добавлен", user.getId());

        return user;
    }

    @Override
    public User update(User user) {
        if (users.get(user.getId()) == null)
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", user.getId()));

        if (user.getName() == null || user.getName().isBlank()) {
            log.info("При обновлени имя для отображения пустое, поле name заполняется логином");
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        log.info("Пользователь с id={} обновлен", user.getId());
        return user;
    }

    @Override
    public Set<Long> findAllFriendsIdByUserId(Long userId) {
        return null;
    }

    @Override
    public Collection<User> getAllFriendsByUserId(Long userId) {
        return List.of();
    }

    @Override
    public boolean friendshipIsConfirmed(Long userId1, Long userId2) {
        return false;
    }

    @Override
    public boolean addFriendship(Long userId1, Long userId2) {
        return false;
    }

    @Override
    public void removeFriendship(Long userId1, Long userId2) {

    }

    @Override
    public Collection<User> getCommonFriends(Long userId1, Long userId2) {
        return List.of();
    }
}
