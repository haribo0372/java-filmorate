package ru.yandex.practicum.filmorate.dto.mapper;

import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class GenreMapper {
    public static GenreDto genreToGenreDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }

    public static Collection<GenreDto> genresToGenresDto(Collection<Genre> genres) {
        return genres.stream()
                .map(GenreMapper::genreToGenreDto)
                .sorted(Comparator.comparing(GenreDto::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
