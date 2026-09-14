package com.gandhimart.service;

import com.gandhimart.dao.UserDAO;
import com.gandhimart.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void register(
            String name,
            String email,
            String password,
            String role) throws SQLException {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException(
                    "Password must be at least 6 characters"
            );
        }

        if (role == null ||
                (!role.equals("BUYER") && !role.equals("SELLER"))) {
            throw new IllegalArgumentException("Invalid role");
        }

        email = email.trim().toLowerCase();

        if (userDAO.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        String passwordHash = BCrypt.hashpw(
                password,
                BCrypt.gensalt()
        );

        User user = new User();

        user.setName(name.trim());
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setRole(role);

        userDAO.create(user);
    }
}