package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class BaseDbStorage<T> {
    protected final JdbcTemplate jdbcTemplate;
    protected final RowMapper<T> rowMapper;

    protected List<T> findMany(String query, Object... params) {
        return jdbcTemplate.query(query, rowMapper, params);
    }

    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbcTemplate.queryForObject(query, rowMapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected int update(String query, Object... params) {
        return jdbcTemplate.update(query, params);
    }

    protected Long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        return Optional.ofNullable(keyHolder.getKeyAs(Long.class)).orElseThrow(
                () -> new InternalServerException("Не удалось сохранить данные"));
    }

    protected boolean insertOne(String query, Object... params) {
        return jdbcTemplate.update(query, params) != 0;
    }
}
