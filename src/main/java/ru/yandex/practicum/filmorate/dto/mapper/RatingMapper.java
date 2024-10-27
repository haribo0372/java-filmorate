package ru.yandex.practicum.filmorate.dto.mapper;

import ru.yandex.practicum.filmorate.dto.rating.RatingDto;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class RatingMapper {
    public static RatingDto ratingToRatingDto(Rating rating) {
        return new RatingDto(rating.getId(), rating.getName());
    }

    public static Collection<RatingDto> ratingsToGenresDto(Collection<Rating> ratings) {
        return ratings.stream()
                .map(RatingMapper::ratingToRatingDto)
                .sorted(Comparator.comparing(RatingDto::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
