package ru.yandex.practicum.filmorate.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotations.WithoutSpaces;


public class WithoutSpacesValidator implements ConstraintValidator<WithoutSpaces, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !s.matches(".*\\s.*");
    }
}
