package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.annotations.DateAfter;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


import java.time.LocalDate;

public class DateAfterValidator implements ConstraintValidator<DateAfter, LocalDate> {

    private static final LocalDate REFERENCE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }
        return !date.isBefore(REFERENCE_DATE);
    }
}
