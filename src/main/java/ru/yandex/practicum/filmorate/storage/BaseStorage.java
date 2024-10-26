package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface BaseStorage<T, K> {
    Collection<T> findAll();

    T findById(K id);

    T remove(K id);

    T save(T entity);

    T update(T entity);
}
