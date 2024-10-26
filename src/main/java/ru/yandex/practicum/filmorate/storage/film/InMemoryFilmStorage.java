package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
//@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private Long serialId = 0L;

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film findById(Long id) {
        Film film = films.get(id);
        if (film == null)
            throw new NotFoundException(String.format("Фильм с id=%d не найден", id));

        return film;
    }

    @Override
    public Film remove(Long id) {
        if (films.get(id) == null)
            throw new NotFoundException(String.format("Фильм с id=%d не найден", id));

        return films.remove(id);
    }

    @Override
    public Film save(Film film) {
        film.setId(++serialId);
        films.put(film.getId(), film);
        log.info("Фильм с id={} добавлен", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (films.get(film.getId()) == null)
            throw new NotFoundException(String.format("Фильм с id=%d не найден", film.getId()));

        films.put(film.getId(), film);
        log.info("Фильм с id={} обновлен", film.getId());

        return film;
    }

    @Override
    public boolean linkFilmToUser(Long filmId, Long userId) {
        return false;
    }

    @Override
    public boolean deleteLinkFilmToUser(Long filmId, Long userId) {
        return false;
    }

    @Override
    public Set<Long> findAllLikesByFilmId(Long filmId) {
        return Set.of();
    }

    @Override
    public Set<Genre> findAllGenresByFilmId(Long filmId) {
        return Set.of();
    }
}
