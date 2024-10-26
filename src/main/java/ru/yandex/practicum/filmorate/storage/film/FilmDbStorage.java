package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreRepository;
import ru.yandex.practicum.filmorate.storage.rating.RatingRepository;
import ru.yandex.practicum.filmorate.util.sql.type.Interval;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.util.sql.cast.TimestampCast.castFromLocalDate;

@Slf4j
@Component
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration," +
            " f.rating_id AS rating_id, r.name AS rating_name FROM t_film f JOIN t_rating r ON f.rating_id = r.id";
    private static final String FIND_BY_ID_QUERY = FIND_ALL_QUERY + " WHERE f.id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM t_film f WHERE f.id = ?";
    private static final String UPDATE_BY_ID_QUERY = "UPDATE t_film SET" +
            " name = ?, description = ?, releaseDate = ?, duration = ?, rating_id = ? WHERE id = ?";
    private static final String INSERT_QUERY_WITH_RATING = "INSERT INTO t_film (name, description," +
            " releaseDate, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_QUERY = "INSERT INTO t_film (name, description," +
            " releaseDate, duration) VALUES (?, ?, ?, ?)";

    private static final String INSERT_RELATED_FILM_AND_USER = "INSERT INTO t_film_user (film_id, user_id) " +
            "VALUES (?, ?)";
    private static final String DELETE_RELATED_FILM_AND_USER = "DELETE FROM t_film_user f_u " +
            "WHERE f_u.film_id = ? AND f_u.user_id = ?";

    private static final String FIND_ALL_LIKES_BY_FILM_ID = "SELECT user_id FROM t_film_user WHERE film_id = ?";

    private final GenreRepository genreRepository;
    private final RatingRepository ratingRepository;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Film> rowMapper, GenreRepository genreRepository, RatingRepository ratingRepository) {
        super(jdbcTemplate, rowMapper);
        this.genreRepository = genreRepository;
        this.ratingRepository = ratingRepository;
    }

    @Override
    public Collection<Film> findAll() {
        Collection<Film> films = findMany(FIND_ALL_QUERY);
        films.forEach(this::fillFilmLikesAndGenres);
        return films;
    }

    @Override
    public Film findById(Long id) {
        Film film = findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с id=%d не найден", id)));

        return fillFilmLikesAndGenres(film);
    }

    @Override
    public Film remove(Long id) {
        Film film = fillFilmLikesAndGenres(findById(id));
        int removedAmount = update(DELETE_BY_ID_QUERY, id);
        if (removedAmount <= 0)
            throw new InternalServerException("Не удалось удалить данные");

        return film;
    }

    @Override
    public Film save(Film film) {
        Interval interval = Interval.fromSeconds(film.getDuration());

        Rating rating = ratingRepository.findById400(film.getRating().getId());

        Long filmId =
                insert(
                        INSERT_QUERY_WITH_RATING,
                        film.getName(),
                        film.getDescription(),
                        castFromLocalDate(film.getReleaseDate()),
                        interval.toStringRepresentation(),
                        rating.getId()
                );

        Set<Genre> genres = film.getGenres();
        if (genres != null)
            genres = genres.stream()
                    .map(genre -> {
                        Genre storageGenre = genreRepository.findById400(genre.getId());
                        linkFilmToGenre(filmId, storageGenre.getId());
                        return storageGenre;
                    })
                    .collect(Collectors.toSet());

        film.setGenres(genres);
        film.setId(filmId);

        return film;
    }

    private void linkFilmToGenre(Long filmId, Long genreId) {
        genreRepository.linkFilmToGenre(filmId, genreId);
    }

    @Override
    public boolean linkFilmToUser(Long filmId, Long userId) {
        return insertOne(INSERT_RELATED_FILM_AND_USER, filmId, userId);
    }

    @Override
    public boolean deleteLinkFilmToUser(Long filmId, Long userId) {
        return update(DELETE_RELATED_FILM_AND_USER, filmId, userId) > 0;
    }

    @Override
    public Set<Long> findAllLikesByFilmId(Long filmId) {
        RowMapper<Long> mapper = (resultSet, rowNum) -> resultSet.getLong(1);

        return new HashSet<>(jdbcTemplate.query(FIND_ALL_LIKES_BY_FILM_ID, mapper, filmId));
    }

    @Override
    public Set<Genre> findAllGenresByFilmId(Long filmId) {
        return genreRepository.findGenresByFilmId(filmId);
    }

    @Override
    public Film update(Film film) {
        Long id = film.getId();

        Interval interval = Interval.fromSeconds(film.getDuration());

        Rating rating = ratingRepository.findById400(film.getRating().getId());

        int removedAmount = update(
                UPDATE_BY_ID_QUERY,
                film.getName(),
                film.getDescription(),
                castFromLocalDate(film.getReleaseDate()),
                interval.toStringRepresentation(),
                rating.getId(),
                id
        );

        if (removedAmount <= 0)
            throw new InternalServerException("Не удалось обновить данные");

        return fillFilmLikesAndGenres(film);
    }

    private Film fillFilmLikesAndGenres(Film film) {
        Set<Long> likes = findAllLikesByFilmId(film.getId());
        Set<Genre> genres = findAllGenresByFilmId(film.getId());

        film.setLikes(likes);
        film.setGenres(genres);

        return film;
    }
}
