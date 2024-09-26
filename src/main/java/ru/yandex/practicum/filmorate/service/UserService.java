package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User addToFriends(Long userId1, Long userId2) {
        User user1 = userStorage.findById(userId1);
        User user2 = userStorage.findById(userId2);

        user1.addFriend(userId2);
        user2.addFriend(userId1);
        return user1;
    }

    public User removeFromFriends(Long userId1, Long userId2) {
        User user1 = userStorage.findById(userId1);
        User user2 = userStorage.findById(userId2);

        user1.removeFromFriends(userId2);
        user2.removeFromFriends(userId1);

        return user1;
    }

    public Collection<User> getCommonFriends(Long userId1, Long userId2) {
        Set<Long> friendsUser1 = userStorage.findById(userId1).getFriends();
        Set<Long> friendsUser2 = userStorage.findById(userId2).getFriends();

        return friendsUser1.stream()
                .filter(friendsUser2::contains)
                .map(userStorage::findById)
                .toList();
    }

    public Collection<User> getFriends(Long id) {
        return userStorage.findById(id).getFriends().stream().map(userStorage::findById).toList();
    }
}
