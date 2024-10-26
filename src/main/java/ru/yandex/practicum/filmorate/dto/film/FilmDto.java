package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Rating mpa;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Genre> genres;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Long> likes;
}
