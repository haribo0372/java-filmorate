package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Collection;
import java.util.Set;

public interface UserStorage extends BaseStorage<User, Long> {
    Set<Long> findAllFriendsIdByUserId(Long userId);

    Collection<User> getAllFriendsByUserId(Long userId);

    boolean friendshipIsConfirmed(Long userId1, Long userId2);

    boolean addFriendship(Long userId1, Long userId2);

    void removeFriendship(Long userId1, Long userId2);

    Collection<User> getCommonFriends(Long userId1, Long userId2);
}
