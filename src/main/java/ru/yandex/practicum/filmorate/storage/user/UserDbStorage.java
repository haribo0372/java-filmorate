package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.util.sql.cast.TimestampCast;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    private static final RowMapper<Friendship> mapperFriendship =
            (rs, rowNum) -> new Friendship(
                    rs.getLong("user_id_send"),
                    rs.getLong("user_id_receive"));

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users u WHERE u.id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM users u WHERE u.id = ?";
    private static final String UPDATE_BY_ID_QUERY = "UPDATE users SET" +
            " email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday)" +
            " VALUES(?, ?, ?, ?)";

    private static final String FIND_ALL_FRIENDSHIP_BY_USER_ID = "SELECT * FROM users_friendship WHERE user_id_send = ?";
    private static final String FIND_FRIENDSHIP = "SELECT * FROM users_friendship " +
            "WHERE user_id_send = ? AND user_id_receive = ?";
    private static final String INSERT_FRIENDSHIP = "INSERT INTO users_friendship " +
            "(user_id_send, user_id_receive) VALUES (?, ?)";
    private static final String DELETE_FRIENDSHIP = "DELETE FROM users_friendship " +
            "WHERE user_id_send = ? AND user_id_receive = ?";

    private static final String FIND_FRIENDS_BY_ID = "SELECT user_id_receive FROM users_friendship " +
            "WHERE user_id_send = ?";
    private static final String FIND_COMMON_FRIENDS =
            "SELECT * FROM users u, users_friendship f, users_friendship o " +
                    "where u.id = f.user_id_receive AND u.id = o.user_id_receive AND f.user_id_send = ? AND o.user_id_send = ?";

    private static final String FIND_ALL_FRIENDS_BY_USER_ID =
            String.format("SELECT u.id, u.email, u.login, u.name, u.birthday " +
                    "FROM users u WHERE u.id IN (%s)", FIND_FRIENDS_BY_ID);

    public UserDbStorage(JdbcTemplate jdbcTemplate, RowMapper<User> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    @Override
    public Collection<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public User findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%s не найден", id)));
    }

    @Override
    public void remove(Long id) {
        int removedAmount = update(DELETE_BY_ID_QUERY, id);
        if (removedAmount <= 0)
            throw new InternalServerException("Не удалось удалить данные");
    }

    @Override
    public User save(User user) {
        long generatedId = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                TimestampCast.castFromLocalDate(user.getBirthday())
        );

        user.setId(generatedId);
        return user;
    }

    @Override
    public User update(User user) {
        Long id = user.getId();

        int removedAmount = update(
                UPDATE_BY_ID_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                TimestampCast.castFromLocalDate(user.getBirthday()),
                id
        );

        if (removedAmount <= 0)
            throw new InternalServerException("Не удалось обновить данные");

        return user;
    }

    private Collection<Friendship> findAllFriendship(Long userId) {
        return jdbcTemplate.query(FIND_ALL_FRIENDSHIP_BY_USER_ID, mapperFriendship, userId);
    }

    @Override
    public Set<Long> findAllFriendsIdByUserId(Long userId) {
        return findAllFriendship(userId).stream()
                .map(Friendship::getUserIdReceive)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<User> getAllFriendsByUserId(Long userId) {
        return findMany(FIND_ALL_FRIENDS_BY_USER_ID, userId);
    }

    @Override
    public boolean friendshipIsConfirmed(Long userId1, Long userId2) {
        try {
            Friendship friendship1 = jdbcTemplate
                    .queryForObject(FIND_FRIENDSHIP, mapperFriendship, userId1, userId2);
            Friendship friendship2 = jdbcTemplate
                    .queryForObject(FIND_FRIENDSHIP, mapperFriendship, userId2, userId1);
            return friendship1 != null && friendship2 != null;
        } catch (EmptyResultDataAccessException ignored) {
            return false;
        }
    }

    private int insertFriendship(Long userId1, Long userId2) {
        return jdbcTemplate.update(INSERT_FRIENDSHIP, userId1, userId2);
    }

    @Override
    public boolean addFriendship(Long userId1, Long userId2) {
        return insertFriendship(userId1, userId2) > 0;
    }

    @Override
    public void removeFriendship(Long userId1, Long userId2) {
        jdbcTemplate.update(DELETE_FRIENDSHIP, userId1, userId2);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId1, Long userId2) {
        return findMany(FIND_COMMON_FRIENDS, userId1, userId2);
    }
}
