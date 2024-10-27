package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class GenreRepository extends BaseDbStorage<Genre> implements BaseStorage<Genre, Long> {
    public static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    public static final String FIND_BY_ID_QUERY = "SELECT * FROM genres g WHERE g.id = ?";
    public static final String FIND_BY_NAME_QUERY = "SELECT * FROM genres g WHERE g.name = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM genres g WHERE g.id = ?";
    private static final String UPDATE_BY_ID_QUERY = "UPDATE genres SET name = ? WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO genres (name) VALUES (?)";

    private static final String INSERT_RELATED_FILM_AND_GENRE =
            "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)";

    private static final String FIND_ALL_GENRES_OF_FILM = """
             SELECT g.id AS id, g.name AS name\s
             FROM films_genres f_g\s
             JOIN genres g ON f_g.genre_id = g.id\s
             WHERE f_g.film_id = ?
            \s""";

    private static final String FIND_RELATED_FILM_GENRE = """
             SELECT g.id AS id, g.name AS name\s
             FROM films_genres f_g\s
             JOIN genres g ON f_g.genre_id = g.id\s
             WHERE f_g.film_id = ? AND f_g.genre_id = ?
            \s""";

    public GenreRepository(JdbcTemplate jdbcTemplate, RowMapper<Genre> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    @Override
    public Collection<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Genre findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id).orElseThrow(
                () -> new NotFoundException(String.format("Жанр с id=%d не найден", id)));
    }

    public Genre findById400(Long id) {
        return findOne(FIND_BY_ID_QUERY, id).orElseThrow(
                () -> new ValidationException(String.format("Жанр с id=%d не найден", id)));
    }

    public Set<Genre> findGenresByFilmId(long filmId) {
        return new HashSet<>(findMany(FIND_ALL_GENRES_OF_FILM, filmId));
    }

    public boolean filmRelatedWithGenre(Long filmId, Long genreId) {
        return findOne(FIND_RELATED_FILM_GENRE, filmId, genreId).isPresent();
    }

    public void linkFilmToGenre(Long filmId, Long genreId) {
        if (!filmRelatedWithGenre(filmId, genreId))
            insertOne(INSERT_RELATED_FILM_AND_GENRE, filmId, genreId);
    }

    public Optional<Genre> findByName(String name) {
        return findOne(FIND_BY_NAME_QUERY, name);
    }

    @Override
    public void remove(Long id) {
        int removedAmount = update(DELETE_BY_ID_QUERY, id);
        if (removedAmount <= 0)
            throw new InternalServerException("Не удалось удалить данные");
    }

    @Override
    public Genre save(Genre entity) {
        Optional<Genre> filmOptional = findByName(entity.getName());
        if (filmOptional.isPresent()) return filmOptional.get();

        Long id = insert(INSERT_QUERY, entity.getName());
        entity.setId(id);
        return entity;
    }

    public Set<Genre> save(List<Genre> genres) {
        return genres.stream().map(this::save).collect(Collectors.toSet());
    }

    @Override
    public Genre update(Genre entity) {
        Long id = entity.getId();
        Genre genre = findById(id);
        int removedAmount = update(UPDATE_BY_ID_QUERY, entity.getName(), id);
        if (removedAmount <= 0)
            throw new InternalServerException("Не удалось удалить данные");

        return genre;
    }
}
