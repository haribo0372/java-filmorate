package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RatingRowMapper implements RowMapper<Rating> {
    @Override
    public Rating mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Long ratingId = resultSet.getLong("id");
        String ratingName = resultSet.getString("name");
        return new Rating(ratingId, ratingName);
    }
}