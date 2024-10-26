package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingRepository;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingController {
    private final RatingRepository ratingRepository;

    @GetMapping
    public Collection<Rating> findAll() {
        return ratingRepository.findAll();
    }

    @GetMapping("/{id}")
    public Rating findById(@PathVariable Long id) {
        return ratingRepository.findById(id);
    }
}