package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Friendship {
    private Long userIdSend;
    private Long userIdReceive;
    private boolean confirmed;
}