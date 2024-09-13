package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        System.out.println("Фильм : " + film);
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Название фильма не должно быть пустым : {}", film.getName());
            throw new ValidationException("Название фильма не должно быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Описание фильма не должно содержать больше 200 символов : {}", film.getDescription().length());
            throw new ValidationException("Описание фильма не должно содержать больше 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.warn("Дата релиза должна быть не раньше 28 декабря 1895 года : {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.warn("Продолжительность фильма должна быть положительным числом : {} c",
                    film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм с id={} добавлен", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (film.getId() == null) {
            log.warn("Id должен быть задан");
            throw new ValidationException("Id должен быть задан");
        }

        if (films.get(film.getId()) == null) {
            log.warn("Фильм с id={} не найден", film.getId());
            throw new NotFoundException("Фильм не найден");
        }

        films.put(film.getId(), film);
        log.info("Фильм с id={} обновлен", film.getId());

        return film;
    }

    private long getNextId() {
        long currentMaxFilmId = films.keySet()
                .stream()
                .mapToLong(id -> id).max()
                .orElse(0);
        return ++currentMaxFilmId;
    }
}
