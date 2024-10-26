package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.util.sql.type.Interval;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");

        LocalDateTime timestamp = resultSet.getTimestamp("releaseDate").toLocalDateTime();
        LocalDate releaseDate = LocalDate.of(timestamp.getYear(), timestamp.getMonthValue(), timestamp.getDayOfMonth());

        Duration duration = Interval.fromString(resultSet.getString("duration")).toDuration();

        Rating rating = new Rating(
                resultSet.getLong("rating_id"), resultSet.getString("rating_name"));


        return new Film(id, name, description, releaseDate, duration, null, rating);
    }
}