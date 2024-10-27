package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    public void testFindUserById() {
        User user = new User(null, "test@example.com",
                "test_user", "Test User", LocalDate.of(1990, 1, 1));

        User savedUser = userStorage.save(user);
        Long userId = savedUser.getId();

        User foundUser = userStorage.findById(userId);
        assertThat(foundUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", userId)
                .hasFieldOrPropertyWithValue("email", "test@example.com")
                .hasFieldOrPropertyWithValue("login", "test_user")
                .hasFieldOrPropertyWithValue("name", "Test User");
    }

    @Test
    public void testFindAll() {
        User user1 = new User(null, "user1@example.com",
                "user1", "User One", LocalDate.of(1992, 4, 10));

        User savedUser1 = userStorage.save(user1);

        User user2 = new User(null, "user2@example.com",
                "user2", "User Two", LocalDate.of(1993, 5, 15));
        User savedUser2 = userStorage.save(user2);

        Collection<User> users = userStorage.findAll();
        assertThat(users.size()).isEqualTo(2);

        List<Long> expectedIds = Stream.of(savedUser1.getId(), savedUser2.getId()).sorted(Long::compareTo).toList();
        List<Long> providedIds = users.stream().map(User::getId).sorted(Long::compareTo).toList();

        for (int i = 0; i < expectedIds.size(); i++) {
            assertThat(expectedIds.get(i).equals(providedIds.get(i))).isTrue();
        }
    }

    @Test
    public void testSaveUser() {
        User user = new User(null, "test_save@example.com",
                "save_user", "Save User", LocalDate.of(1985, 5, 15));

        User savedUser = userStorage.save(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test_save@example.com");
        assertThat(savedUser.getLogin()).isEqualTo("save_user");
        assertThat(savedUser.getName()).isEqualTo("Save User");
        assertThat(savedUser.getBirthday()).isEqualTo(LocalDate.of(1985, 5, 15));
    }

    @Test
    public void testUpdateUser() {
        User user = new User(null, "test_update@example.com",
                "update_user", "Update User", LocalDate.of(1975, 3, 25));
        User savedUser = userStorage.save(user);
        Long userId = savedUser.getId();

        savedUser.setEmail("updated_email@example.com");
        savedUser.setName("Updated Name");

        User updatedUser = userStorage.update(savedUser);

        assertThat(updatedUser.getId()).isEqualTo(userId);
        assertThat(updatedUser.getEmail()).isEqualTo("updated_email@example.com");
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
    }

    @Test
    public void testRemoveUser() {
        User user = new User(null, "test_remove@example.com",
                "remove_user", "Remove User", LocalDate.of(1995, 7, 20));
        User savedUser = userStorage.save(user);
        Long userId = savedUser.getId();
        userStorage.remove(userId);
        assertThatThrownBy(() -> userStorage.findById(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Пользователь с id=%s не найден", userId));
    }

    @Test
    public void testAddAndRemoveFriendship() {
        User user1 = new User(null, "user1@example.com",
                "user1", "User One", LocalDate.of(1992, 4, 10));

        User savedUser1 = userStorage.save(user1);

        User user2 = new User(null, "user2@example.com",
                "user2", "User Two", LocalDate.of(1993, 5, 15));
        User savedUser2 = userStorage.save(user2);

        boolean isFriendAdded1 = userStorage.addFriendship(savedUser1.getId(), savedUser2.getId());
        Collection<User> friends1 = userStorage.getAllFriendsByUserId(savedUser1.getId());

        assertThat(isFriendAdded1).isTrue();
        assertThat(friends1.stream()
                .anyMatch(user -> user.getId().equals(savedUser2.getId()))
        ).isTrue();

        assertThat(userStorage.friendshipIsConfirmed(savedUser1.getId(), savedUser2.getId())).isFalse();


        boolean isFriendAdded2 = userStorage.addFriendship(savedUser2.getId(), savedUser1.getId());
        Collection<User> friends2 = userStorage.getAllFriendsByUserId(savedUser2.getId());

        assertThat(isFriendAdded2).isTrue();
        assertThat(friends2.stream()
                .anyMatch(user -> user.getId().equals(savedUser1.getId()))
        ).isTrue();

        assertThat(userStorage.findAllFriendsIdByUserId(savedUser1.getId()).size()).isEqualTo(1);
        assertThat(userStorage.friendshipIsConfirmed(savedUser1.getId(), savedUser2.getId())).isTrue();

        userStorage.removeFriendship(savedUser1.getId(), savedUser2.getId());
        assertThat(userStorage.friendshipIsConfirmed(savedUser1.getId(), savedUser2.getId())).isFalse();
    }

    @Test
    public void testGetCommonFriends() {
        User user1 = new User(null, "user1@example.com",
                "user1", "User One", LocalDate.of(1992, 4, 10));

        User savedUser1 = userStorage.save(user1);

        User user2 = new User(null, "user2@example.com",
                "user2", "User Two", LocalDate.of(1993, 5, 15));
        User savedUser2 = userStorage.save(user2);

        User user3 = new User(null, "user3@example.com",
                "user3", "User Three", LocalDate.of(1994, 9, 19));
        User savedUser3 = userStorage.save(user3);


        assertThat(userStorage.addFriendship(savedUser2.getId(), savedUser3.getId())).isTrue();
        assertThat(userStorage.addFriendship(savedUser1.getId(), savedUser3.getId())).isTrue();

        Collection<User> commonFriends = userStorage.getCommonFriends(savedUser1.getId(), savedUser2.getId());
        assertThat(commonFriends.contains(savedUser3)).isTrue();
    }
}
