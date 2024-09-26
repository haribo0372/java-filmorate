package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(UserController.class)
public class UserControllerTests {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User(
                1L,
                "banana@mail.ru",
                "bananaTheBest",
                "banana",
                LocalDate.now()
        );
    }

    @Test
    public void userCreate() throws Exception {
        when(userService.addUser(any(User.class))).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
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
        user.setEmail("bananas-email-siuu.ru");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    public void userCreateFailLogin() throws Exception {
        user.setLogin("bananas login");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().is(400));

        user.setLogin("     ");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    public void userCreateFailBirthday() throws Exception {
        user.setBirthday(LocalDate.now().plusDays(1));

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    public void userCreateEmptyName() throws Exception {
        user.setName("");
        String requestBody = objectMapper.writeValueAsString(user);

        user.setName(user.getLogin());
        when(userService.addUser(any(User.class))).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(user.getLogin()));
    }

    @Test
    public void userGetAll() throws Exception {
        when(userService.findAll()).thenReturn(Collections.singleton(user));
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
        when(userService.updateUser(any(User.class))).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(user.getName()));
    }

    @Test
    public void userUnknownUpdate() throws Exception {
        when(userService.updateUser(any(User.class))).thenThrow(new NotFoundException(""));
        mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().is(404));
    }
}
