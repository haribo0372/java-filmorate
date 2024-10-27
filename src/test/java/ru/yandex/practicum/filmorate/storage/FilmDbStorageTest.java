package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.genre.GenreRepository;
import ru.yandex.practicum.filmorate.storage.genre.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.rating.RatingRepository;
import ru.yandex.practicum.filmorate.storage.rating.RatingRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserRowMapper;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        FilmDbStorage.class, FilmRowMapper.class,
        GenreRowMapper.class, GenreRepository.class,
        RatingRowMapper.class, RatingRepository.class,
        UserDbStorage.class, UserRowMapper.class
})
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final GenreRepository genreRepository;
    private final RatingRepository ratingRepository;
    private final UserDbStorage userStorage;
    private Film film;

    @BeforeEach
    public void setUp() {
        film = new Film(
                null,
                "Test Film",
                "Test Description",
                LocalDate.of(2020, 1, 1),
                Duration.ofMinutes(120),
                null,
                new Rating(1L, null)
        );
    }

    @Test
    public void testFindAll() {
        assertThat(filmStorage.findAll().isEmpty()).isTrue();

        Long currentId = filmStorage.save(film).getId();
        Collection<Film> films = filmStorage.findAll();
        assertThat(films.isEmpty()).isFalse();
        assertThat(films.stream().findAny().get().getId().equals(currentId)).isTrue();
    }

    @Test
    public void testFindById() {
        Film savedFilm = filmStorage.save(film);

        Film foundFilm = filmStorage.findById(savedFilm.getId());
        assertFilmsEqual(savedFilm, foundFilm);
    }

    @Test
    public void testRemove() {
        Film savedFilm = filmStorage.save(film);
        Long filmId = savedFilm.getId();

        filmStorage.remove(filmId);

        assertThatThrownBy(() -> filmStorage.findById(filmId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void testSave() {
        Film savedFilm = filmStorage.save(film);

        assertThat(savedFilm).isNotNull();
        assertThat(savedFilm.getId()).isNotNull();
        assertThat(filmStorage.findAll().isEmpty()).isFalse();
    }

    @Test
    public void testUpdate() {
        Film savedFilm = filmStorage.save(film);

        savedFilm.setName("Updated Film");
        savedFilm.setDescription("Updated Description");
        Film updatedFilm = filmStorage.update(savedFilm);

        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm.getName()).isEqualTo("Updated Film");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    public void testLinkFilmToUser() {
        Film savedFilm = filmStorage.save(film);
        Long filmId = savedFilm.getId();

        User someUser = new User(null, "user1@example.com",
                "user1", "User One", LocalDate.of(1992, 4, 10));
        Long idSavedUser = userStorage.save(someUser).getId();

        boolean isLinked = filmStorage.linkFilmToUser(filmId, idSavedUser);
        assertThat(isLinked).isTrue();
    }

    @Test
    public void testDeleteLinkFilmToUser() {
        Film savedFilm = filmStorage.save(film);
        Long filmId = savedFilm.getId();

        User someUser = new User(null, "user1@example.com",
                "user1", "User One", LocalDate.of(1992, 4, 10));
        Long idSavedUser = userStorage.save(someUser).getId();

        filmStorage.linkFilmToUser(filmId, idSavedUser);
        boolean isDeleted = filmStorage.deleteLinkFilmToUser(filmId, idSavedUser);
        assertThat(isDeleted).isTrue();
    }

    @Test
    public void testFindAllLikesByFilmId() {
        Film savedFilm = filmStorage.save(film);
        Long filmId = savedFilm.getId();
        User someUser = new User(null, "user1@example.com",
                "user1", "User One", LocalDate.of(1992, 4, 10));
        Long idSavedUser = userStorage.save(someUser).getId();

        filmStorage.linkFilmToUser(filmId, idSavedUser);
        Set<Long> likes = filmStorage.findAllLikesByFilmId(filmId);
        assertThat(likes.contains(idSavedUser)).isTrue();
    }

    @Test
    public void testFindAllGenresByFilmId() {
        film.setGenres(new HashSet<>(genreRepository.findAll()));
        Film savedFilm = filmStorage.save(film);

        Set<Genre> genres = filmStorage.findAllGenresByFilmId(savedFilm.getId());
        assertThat(genres).isNotNull();
    }

    @Test
    public void testInitializationsGenres() {
        String[] expectedGenreNames = new String[]{
                "Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик"
        };

        Set<Genre> genres = new HashSet<>(genreRepository.findAll());
        assertThat(genres.size()).isEqualTo(expectedGenreNames.length);

        for (String expectedGenreName : expectedGenreNames)
            assertThat(genres.stream().anyMatch(genre -> genre.getName().equals(expectedGenreName))).isTrue();
    }


    @Test
    public void testInitializationsMPA() {
        String[] expectedRatingNames = new String[]{
                "G", "PG", "PG-13", "R", "NC-17"
        };

        Set<Rating> ratings = new HashSet<>(ratingRepository.findAll());
        assertThat(ratings.size()).isEqualTo(expectedRatingNames.length);

        for (String expectedRatingName : expectedRatingNames)
            assertThat(ratings.stream().anyMatch(rating -> rating.getName().equals(expectedRatingName))).isTrue();
    }

    private void assertFilmsEqual(Film film1, Film film2) {
        assertThat(film1.getName().equals(film2.getName())).isTrue();
        assertThat(film1.getDescription().equals(film2.getDescription())).isTrue();
        assertThat(film1.getDuration() == film2.getDuration()).isTrue();
        assertThat(film1.getReleaseDate().equals(film2.getReleaseDate())).isTrue();
    }
}
