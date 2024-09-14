package ru.yandex.practicum.filmorate.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotations.DurationIsPositiveOrZero;

import java.time.Duration;

public class DurationPositiveOrZeroValidator implements ConstraintValidator<DurationIsPositiveOrZero, Duration> {
    @Override
    public boolean isValid(Duration duration, ConstraintValidatorContext context) {
        if (duration == null) {
            return true;
        }
        return duration.isPositive();
    }
}

