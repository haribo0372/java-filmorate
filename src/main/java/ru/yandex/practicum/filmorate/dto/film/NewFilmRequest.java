package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.annotations.DateAfter;
import ru.yandex.practicum.filmorate.annotations.DurationIsPositiveOrZero;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewFilmRequest {
    @NotBlank(message = "Название фильма не должно быть пустым")
    private String name;

    @Size(max = 200, message = "Описание фильма не должно содержать больше 200 символов")
    private String description;

    @NotNull(message = "Дата релиза не должна быть пустой")
    @DateAfter(message = "Дата релиза должна быть не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;

    @DurationIsPositiveOrZero(message = "Продолжительность фильма должна быть положительным числом")
    private Duration duration;

    @NotNull(message = "Рейтинг фильма не должен быть пустым")
    private Rating mpa;

    private Set<Genre> genres;
}
