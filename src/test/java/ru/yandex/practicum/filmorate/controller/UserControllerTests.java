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
import ru.yandex.practicum.filmorate.dto.mapper.UserMapper;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class UserControllerTests {
    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private NewUserRequest newUserRequest = new NewUserRequest(
            "banana@mail.ru",
            "bananaTheBest",
            "banana",
            LocalDate.now()
    );

    private UpdateUserRequest updateUserRequest = new UpdateUserRequest(
            1L,
            "banana@mail.ru",
            "bananaTheBest",
            "banana",
            LocalDate.now()
    );

    private User user;

    @BeforeEach
    public void setUp() {
        user = UserMapper.fromNewUserRequestToUser(newUserRequest);
    }

    @Test
    public void userCreate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(user.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value(user.getLogin()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(user.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday")
                        .value(user.getBirthday().format(dateTimeFormatter)));
    }

    @Test
    public void userCreateFailEmail() throws Exception {
        newUserRequest.setEmail("bananas-email-siuu.ru");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserRequest)))
                .andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    public void userCreateFailLogin() throws Exception {
        newUserRequest.setLogin("bananas login");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserRequest)))
                .andExpect(MockMvcResultMatchers.status().is(400));

        newUserRequest.setLogin("     ");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserRequest)))
                .andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    public void userCreateFailBirthday() throws Exception {
        newUserRequest.setBirthday(LocalDate.now().plusDays(1));

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserRequest)))
                .andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    public void userCreateEmptyName() throws Exception {
        newUserRequest.setName("");
        String requestBody = objectMapper.writeValueAsString(newUserRequest);

        newUserRequest.setName(user.getLogin());
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(newUserRequest.getLogin()));
    }

    @Test
    public void userGetAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[-1].id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$[-1].email").value(user.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[-1].login").value(user.getLogin()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[-1].name").value(user.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[-1].birthday")
                        .value(user.getBirthday().format(dateTimeFormatter)));
    }

    @Test
    public void userUpdate() throws Exception {
        updateUserRequest.setName("UPDATED NAME");
        mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(updateUserRequest.getName()));
    }

    @Test
    public void userUnknownUpdate() throws Exception {
        updateUserRequest.setId(500L);
        mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(MockMvcResultMatchers.status().is(404));
    }
}
