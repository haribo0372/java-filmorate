package ru.yandex.practicum.filmorate.dto.mapper;

import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FilmMapper {
    public static Film newFilmRequestToFilm(NewFilmRequest request) {
        return new Film(
                null,
                request.getName(),
                request.getDescription(),
                request.getReleaseDate(),
                request.getDuration(),
                request.getGenres(),
                request.getMpa());
    }


    public static Film updateFilmRequestToFilm(UpdateFilmRequest request) {
        return new Film(
                request.getId(),
                request.getName(),
                request.getDescription(),
                request.getReleaseDate(),
                request.getDuration(),
                request.getGenres(),
                request.getMpa());
    }

    public static FilmDto filmToFilmDto(Film film) {
        Set<Genre> sortedGenres = film.getGenres();
        if (sortedGenres != null)
            sortedGenres = sortedGenres.stream()
                    .sorted(Comparator.comparingLong(Genre::getId))
                    .collect(Collectors.toCollection(LinkedHashSet::new));

        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setMpa(film.getRating());
        filmDto.setDuration(film.getDuration());
        filmDto.setGenres(sortedGenres);
        return filmDto;
    }

    public static Collection<FilmDto> filmsToFilmsDto(Collection<Film> films) {
        return films.stream().map(FilmMapper::filmToFilmDto).toList();
    }

    private static Set<Genre> castLongsToGenres(Collection<Long> ids) {
        if (ids == null) return null;
        return ids.stream()
                .map(id -> new Genre(id, null)).collect(Collectors.toSet());
    }
}
