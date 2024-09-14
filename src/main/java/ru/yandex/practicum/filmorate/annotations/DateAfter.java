package ru.yandex.practicum.filmorate.annotations;

import ru.yandex.practicum.filmorate.validators.DateAfterValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateAfterValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DateAfter {

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

