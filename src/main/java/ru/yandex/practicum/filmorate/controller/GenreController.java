package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreRepository;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreRepository genreRepository;

    @GetMapping
    public Collection<Genre> findAll() {
        return genreRepository.findAll();
    }

    @GetMapping("/{id}")
    public Genre findById(@PathVariable Long id) {
        return genreRepository.findById(id);
    }
}
