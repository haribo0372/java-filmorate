package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RatingRepository extends BaseDbStorage<Rating> implements BaseStorage<Rating, Long> {
    public static final String FIND_ALL_QUERY = "SELECT * FROM t_rating";
    public static final String FIND_BY_ID_QUERY = "SELECT * FROM t_rating r WHERE r.id = ?";
    public static final String FIND_BY_NAME_QUERY = "SELECT * FROM t_rating r WHERE r.name = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM t_rating r WHERE r.id = ?";
    private static final String UPDATE_BY_ID_QUERY = "UPDATE t_rating SET name = ? WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO t_rating (name) VALUES (?)";

    public RatingRepository(JdbcTemplate jdbcTemplate, RowMapper<Rating> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    @Override
    public Collection<Rating> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Rating findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id).orElseThrow(
                () -> new NotFoundException(String.format("Рейтинг с id=%d не найден", id)));
    }

    public Rating findById400(Long id) {
        return findOne(FIND_BY_ID_QUERY, id).orElseThrow(
                () -> new ValidationException(String.format("Рейтинг с id=%d не найден", id)));
    }

    public Optional<Rating> findByName(String name) {
        return findOne(FIND_BY_NAME_QUERY, name);
    }

    @Override
    public Rating remove(Long id) {
        Rating rating = findById(id);
        int removedAmount = update(DELETE_BY_ID_QUERY, id);
        if (removedAmount <= 0)
            throw new InternalServerException("Не удалось удалить данные");

        return rating;
    }

    @Override
    public Rating save(Rating entity) {
        Optional<Rating> filmOptional = findByName(entity.getName());
        if (filmOptional.isPresent()) return filmOptional.get();

        Long id = insert(INSERT_QUERY, entity.getName());
        entity.setId(id);
        return entity;
    }

    public Set<Rating> save(List<Rating> genres) {
        return genres.stream().map(this::save).collect(Collectors.toSet());
    }

    @Override
    public Rating update(Rating entity) {
        Long id = entity.getId();
        Rating rating = findById(id);
        int removedAmount = update(UPDATE_BY_ID_QUERY, entity.getName(), id);
        if (removedAmount <= 0)
            throw new InternalServerException("Не удалось удалить данные");

        return rating;
    }
}
