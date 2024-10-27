package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        String login = resultSet.getString("login");

        LocalDateTime timestamp = resultSet.getTimestamp("birthday").toLocalDateTime();
        LocalDate birthday = LocalDate.of(timestamp.getYear(), timestamp.getMonthValue(), timestamp.getDayOfMonth());
        return new User(id, email, login, name, birthday);
    }
}