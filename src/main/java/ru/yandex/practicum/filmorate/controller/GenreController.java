package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.genre.GenreRepository;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreRepository genreRepository;

    @GetMapping
    public Collection<GenreDto> findAll() {
        return GenreMapper.genresToGenresDto(genreRepository.findAll());
    }

    @GetMapping("/{id}")
    public GenreDto findById(@PathVariable Long id) {
        return GenreMapper.genreToGenreDto(genreRepository.findById(id));
    }
}
