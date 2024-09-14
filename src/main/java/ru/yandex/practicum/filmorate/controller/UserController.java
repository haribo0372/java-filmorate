package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя для отображения пустое, поле name заполняется логином");
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь с id={} добавлен", user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {

        if (users.get(user.getId()) == null) {
            log.warn("Пользователь с id={} не найден", user.getId());
            throw new NotFoundException("Пользователь не найден");
        }

        users.put(user.getId(), user);
        log.info("Пользователь с id={} обновлен", user.getId());
        return user;
    }

    private long getNextId() {
        long currentMaxUserId = users.keySet()
                .stream()
                .mapToLong(id -> id).max()
                .orElse(0);
        return ++currentMaxUserId;
    }
}
