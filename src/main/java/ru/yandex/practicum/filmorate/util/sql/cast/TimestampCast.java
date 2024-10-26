package ru.yandex.practicum.filmorate.util.sql.cast;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class TimestampCast {
    public static final ZoneOffset currentZone = ZoneOffset.UTC;

    public static Timestamp castFromLocalDate(LocalDate localDate) {
        LocalDateTime localDateTime = localDate.atStartOfDay();

        OffsetDateTime offsetDateTime = localDateTime.atOffset(currentZone);

        return Timestamp.from(offsetDateTime.toInstant());
    }
}
