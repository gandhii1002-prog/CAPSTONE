package com.gandhimart.service;

import com.gandhimart.dao.UserDAO;
import com.gandhimart.model.User;

import java.sql.SQLException;
import java.util.Optional;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Registers a new user after validating the input.
     */
    public void register(
            String name,
            String email,
            String passwordHash,
            String role) throws SQLException {

        validateRegistration(name, email, role);

        Optional<User> existingUser =
                userDAO.findByEmail(email);

        if (existingUser.isPresent()) {
            throw new IllegalArgumentException(
                    "An account with this email already exists."
            );
        }

        User user = new User();

        user.setName(name.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPasswordHash(passwordHash);
        user.setRole(role);

        userDAO.create(user);
    }

    /**
     * Finds a user by email.
     */
    public Optional<User> findByEmail(String email)
            throws SQLException {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required."
            );
        }

        return userDAO.findByEmail(
                email.trim().toLowerCase()
        );
    }

    private void validateRegistration(
            String name,
            String email,
            String role) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Name is required."
            );
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required."
            );
        }

        if (!email.contains("@")) {
            throw new IllegalArgumentException(
                    "Enter a valid email address."
            );
        }

        if (!"BUYER".equals(role)
                && !"SELLER".equals(role)) {

            throw new IllegalArgumentException(
                    "Only BUYER or SELLER registration is allowed."
            );
        }
    }
}