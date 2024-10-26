package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class UpdateFilmRequest {
    @NotNull(message = "Id для сущности должен быть указан")
    private Long id;

    private String name;

    @Size(max = 200, message = "Описание фильма не должно содержать больше 200 символов")
    private String description;

    @DateAfter(message = "Дата релиза должна быть не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;

    @DurationIsPositiveOrZero(message = "Продолжительность фильма должна быть положительным числом")
    private Duration duration;

    private Set<Genre> genres;
    private Rating mpa;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Long> likes;

    public boolean hasName() {
        return name != null;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasDuration() {
        return duration != null;
    }

    public boolean hasRating() {
        return mpa != null;
    }
}
