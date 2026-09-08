package com.gandhimart.dao;

import com.gandhimart.model.User;

import java.sql.SQLException;
import java.util.Optional;

public interface UserDAO {

    void create(User user) throws SQLException;

    Optional<User> findByEmail(String email) throws SQLException;

    Optional<User> findById(Long id) throws SQLException;
}