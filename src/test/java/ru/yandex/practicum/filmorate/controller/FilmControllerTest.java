package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.IntStream;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FilmService filmService;

    @Autowired
    private ObjectMapper objectMapper;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Film film;
    private NewFilmRequest newFilmRequest = new NewFilmRequest(
            "bananaFilm",
            "bananaTheBest",
            LocalDate.now(),
            Duration.ofHours(2),
            new Rating(1L, null),
            Set.of()
    );

    private UpdateFilmRequest updateFilmRequest = new UpdateFilmRequest(
            1L, newFilmRequest.getName(), newFilmRequest.getDescription(),
            newFilmRequest.getReleaseDate(), newFilmRequest.getDuration(),
            newFilmRequest.getGenres(), newFilmRequest.getMpa()
    );


    @BeforeEach
    public void setUp() {
        film = FilmMapper.newFilmRequestToFilm(newFilmRequest);
    }

    @Test
    public void filmCreate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFilmRequest)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(film.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(film.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value(film.getReleaseDate().format(dateTimeFormatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(film.getDuration()));
    }

    @Test
    public void filmCreateFailName() throws Exception {
        film.setName("");
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    public void filmCreateFailDescription() throws Exception {
        StringBuilder sb = new StringBuilder("*");
        IntStream.range(0, 200).forEach(i -> sb.append("*"));

        film.setDescription(sb.toString());
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    public void filmCreateFailReleaseDate() throws Exception {
        film.setReleaseDate(LocalDate.of(1895, 12, 28).minusDays(1));
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    public void filmCreateFailDuration() throws Exception {
        film.setDuration(Duration.ofHours(-1));
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    public void filmGetAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/films"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[-1].id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$[-1].name").value(film.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[-1].description").value(film.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[-1].releaseDate").value(film.getReleaseDate().format(dateTimeFormatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[-1].duration").value(film.getDuration()));
    }

    @Test
    public void filmUpdate() throws Exception {
        updateFilmRequest.setName("UPDATED NAME");
        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateFilmRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(updateFilmRequest.getName()));
    }

    @Test
    public void filmUnknownUpdate() throws Exception {
        updateFilmRequest.setId(500L);
        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateFilmRequest)))
                .andExpect(MockMvcResultMatchers.status().is(404));
    }
}
