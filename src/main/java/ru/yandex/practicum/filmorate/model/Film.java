package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotations.DateAfter;
import ru.yandex.practicum.filmorate.annotations.DurationIsPositiveOrZero;

import java.time.Duration;
import java.time.LocalDate;

@Data
@AllArgsConstructor
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

    public long getDuration() {
        return duration.getSeconds();
    }
}
