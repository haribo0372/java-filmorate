package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Duration;
import java.util.Collection;
import java.util.Comparator;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmDto addLike(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId);
        userStorage.findById(userId);
        filmStorage.linkFilmToUser(filmId, userId);

        log.info("Фильму с id={} добавлен лайк от пользователя с id {}", filmId, userId);
        return FilmMapper.filmToFilmDto(film);
    }

    public FilmDto findById(Long filmId) {
        return FilmMapper.filmToFilmDto(filmStorage.findById(filmId));
    }

    public FilmDto save(NewFilmRequest request) {
        Film film = FilmMapper.newFilmRequestToFilm(request);
        Film savedFilm = filmStorage.save(film);
        log.info("Фильм с id={} сохранен", savedFilm.getId());
        return FilmMapper.filmToFilmDto(savedFilm);
    }

    public FilmDto updateFilm(UpdateFilmRequest request) {
        Film film = FilmMapper.updateFilmRequestToFilm(request);
        Film storageFilm = filmStorage.findById(film.getId());

        if (film.getName() != null) storageFilm.setName(film.getName());
        if (film.getDescription() != null) storageFilm.setDescription(film.getDescription());
        if (film.getReleaseDate() != null) storageFilm.setReleaseDate(film.getReleaseDate());
        if (film.getDuration() != 0) storageFilm.setDuration(Duration.ofSeconds(film.getDuration()));

        if (film.getRating() != null && film.getRating().getName() != null)
            storageFilm.setRating(film.getRating());

        Film updatedFilm = filmStorage.update(storageFilm);
        log.info("Фильм с id={} обновлен", updatedFilm.getId());
        return FilmMapper.filmToFilmDto(updatedFilm);
    }

    public Collection<FilmDto> findAll() {
        return FilmMapper.filmsToFilmsDto(filmStorage.findAll());
    }

    public FilmDto removeLike(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId);
        userStorage.findById(userId);
        filmStorage.deleteLinkFilmToUser(filmId, userId);

        log.info("Фильму с id={} удален лайк от пользователя с id {}", filmId, userId);
        return FilmMapper.filmToFilmDto(film);
    }

    public Collection<FilmDto> getMostPopularMovies(Long count) {
        Collection<Film> popularFilms = filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(Film::numberOfLikes).reversed())
                .limit(count)
                .toList();

        log.debug("Возвращены популярные фильмы {}", popularFilms);
        return FilmMapper.filmsToFilmsDto(popularFilms);
    }
}
