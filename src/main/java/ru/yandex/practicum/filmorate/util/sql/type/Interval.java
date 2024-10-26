package ru.yandex.practicum.filmorate.util.sql.type;

import lombok.Data;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Data
public class Interval {
    private long hours;
    private long minutes;
    private long seconds;

    public String toStringRepresentation() {
        return String.format("%d:%d:%d", hours, minutes, seconds);
    }

    public Duration toDuration() {
        return Duration
                .ofHours(this.hours)
                .plusMinutes(this.minutes)
                .plusSeconds(this.seconds);
    }

    public static Interval fromSeconds(long seconds) {
        Interval interval = new Interval();
        interval.hours = TimeUnit.SECONDS.toHours(seconds);
        interval.minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60;
        interval.seconds = seconds % 60;
        return interval;
    }

    public static Interval fromString(String intervalString) {
//        INTERVAL '0:01:40' HOUR TO SECOND
        String time = intervalString.split("\\s+")[1];
        time = time.substring(1, time.length() - 1);
        String[] parts = time.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid interval format: " + intervalString);
        }

        long hours = Long.parseLong(parts[0]);
        long minutes = Long.parseLong(parts[1]);
        long seconds = Long.parseLong(parts[2]);

        Interval interval = new Interval();
        interval.setHours(hours);
        interval.setMinutes(minutes);
        interval.setSeconds(seconds);
        return interval;
    }
}
