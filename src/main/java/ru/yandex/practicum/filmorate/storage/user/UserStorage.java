package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    User findById(Long id);

    User remove(Long id);

    User add(User user);

    User update(User user);
}
