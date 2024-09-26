package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.IntStream;

@WebMvcTest(FilmController.class)
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Film film;

    @BeforeEach
    public void setUp() {
        film = new Film(
                null,
                "bananaFilm",
                "bananaTheBest",
                LocalDate.now(),
                Duration.ofHours(2)
        );
    }

    @Test
    public void filmCreate() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.post("/films")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(film)))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(film.getName()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(film.getDescription()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value(film.getReleaseDate().format(dateTimeFormatter)))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(film.getDuration()));
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
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isOk());

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
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();

        film.setId(id);
        film.setName("bananaFilm-2.0");
        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(film.getName()));
    }

    @Test
    public void filmUnknownUpdate() throws Exception {
        film.setId(9999L);
        mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }
}
