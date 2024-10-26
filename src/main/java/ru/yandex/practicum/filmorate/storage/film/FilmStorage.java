package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Set;

public interface FilmStorage extends BaseStorage<Film, Long> {

    boolean linkFilmToUser(Long filmId, Long userId);

    boolean deleteLinkFilmToUser(Long filmId, Long userId);

    Set<Long> findAllLikesByFilmId(Long filmId);

    Set<Genre> findAllGenresByFilmId(Long filmId);
}
