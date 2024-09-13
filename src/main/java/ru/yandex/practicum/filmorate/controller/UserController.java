package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
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
    public User create(@RequestBody User user) {
        String userEmail = user.getEmail();
        if (userEmail == null || userEmail.isBlank() || !userEmail.contains("@")) {
            log.warn("Электронная почта не может быть пустой и должна содержать символ @ : {}", userEmail);
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        String userLogin = user.getLogin();
        if (userLogin == null || userLogin.isBlank() || userLogin.matches(".*\\s.*")) {
            log.warn("Логин не может быть пустым и содержать пробелы : {}", userLogin);
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения не может быть в будущем : {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

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
    public User update(@RequestBody User user) {
        if (user.getId() == null) {
            log.warn("Id должен быть задан");
            throw new ValidationException("Id должен быть задан");
        }
        if (users.get(user.getId()) == null) {
            log.warn("Пользователь с id={} не найден", user.getId());
            throw new NotFoundException("Пользователь не найден");
        }

//        users.values().stream()
//                .filter(i -> i.getLogin().equals(user.getLogin()) && !i.equals(user)).findAny().ifPresent( i -> {
//                    log.warn("Пользователь с таким логином существует : {}", user.getLogin());
//                    throw new ValidationException("Этот логин уже занят");
//                });

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
