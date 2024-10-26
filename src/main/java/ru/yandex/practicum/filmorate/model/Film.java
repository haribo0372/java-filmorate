package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;

import lombok.Data;
import ru.yandex.practicum.filmorate.annotations.DateAfter;
import ru.yandex.practicum.filmorate.annotations.DurationIsPositiveOrZero;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Long id;

    @NotBlank(message = "Название фильма не должно быть пустым")
    private String name;

    @Size(max = 200, message = "Описание фильма не должно содержать больше 200 символов")
    private String description;

    @NotNull(message = "Дата релиза не должна быть пустой")
    @DateAfter(message = "Дата релиза должна быть не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;

    @DurationIsPositiveOrZero(message = "Продолжительность фильма должна быть положительным числом")
    private Duration duration;

    private Set<Genre> genres;
    private Rating rating;

    private Set<Long> likes;

    public Film(Long id, String name, String description,
                LocalDate releaseDate, Duration duration,
                Set<Genre> genres, Rating rating, Set<Long> likes) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        this.rating = rating;
        this.likes = likes == null ? new HashSet<>() : likes;
    }

    public Film(Long id, String name, String description,
                LocalDate releaseDate, Duration duration,
                Set<Genre> genres, Rating rating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        this.rating = rating;
        this.likes = new HashSet<>();
    }

    public long getDuration() {
        if (duration == null) return 0;
        return duration.getSeconds();
    }

    public void addLike(Long id) {
        likes.add(id);
    }

    public void removeLike(Long id) {
        likes.remove(id);
    }

    public int numberOfLikes() {
        return likes.size();
    }
}
