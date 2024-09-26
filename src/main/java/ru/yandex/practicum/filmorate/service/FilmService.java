package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addLike(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId);
        userStorage.findById(userId);
        film.addLike(userId);

        log.info("Фильму с id={} добавлен лайк от пользователя с id {}", filmId, userId);
        return film;
    }

    public Film removeLike(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId);
        userStorage.findById(userId);
        film.removeLike(userId);

        log.info("Фильму с id={} удален лайк от пользователя с id {}", filmId, userId);
        return film;
    }

    public Collection<Film> getMostPopularMovies(Long count) {
        long limit = count == null ? 10 : count;
        Collection<Film> popularFilms = filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(Film::numberOfLikes).reversed())
                .limit(limit)
                .toList();

        log.debug("Возвращены популярные фильмы {}", popularFilms);
        return popularFilms;
    }
}
