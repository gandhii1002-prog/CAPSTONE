package com.gandhimart.dao;

import com.gandhimart.model.User;
import com.gandhimart.util.DatabaseConfig;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    private final DataSource dataSource;

    public UserDAOImpl() {
        this.dataSource = DatabaseConfig.getDataSource();
    }

    @Override
    public void create(User user) throws SQLException {

        String sql = """
                INSERT INTO users
                (name, email, password_hash, role)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole());

            statement.executeUpdate();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) throws SQLException {

        String sql = """
                SELECT id, name, email, password_hash, role, created_at
                FROM users
                WHERE email = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findById(Long id) throws SQLException {

        String sql = """
                SELECT id, name, email, password_hash, role, created_at
                FROM users
                WHERE id = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    private User mapUser(ResultSet resultSet) throws SQLException {

        User user = new User();

        user.setId(resultSet.getLong("id"));
        user.setName(resultSet.getString("name"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setRole(resultSet.getString("role"));

        Timestamp timestamp = resultSet.getTimestamp("created_at");

        if (timestamp != null) {
            user.setCreatedAt(timestamp.toLocalDateTime());
        }

        return user;
    }
}