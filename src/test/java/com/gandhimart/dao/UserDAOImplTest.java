package com.gandhimart.dao;

import com.gandhimart.model.User;
import com.gandhimart.testutil.H2TestDatabase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOImplTest {

    private DataSource dataSource;
    private UserDAOImpl userDAO;

    @BeforeEach
    void setUp() throws Exception {
        dataSource =
                H2TestDatabase.create();

        userDAO =
                new UserDAOImpl(dataSource);
    }

    @Test
    void createAndFindByEmail() throws Exception {

        User user =
                new User();

        user.setName("DAO Test User");
        user.setEmail("dao@test.com");
        user.setPasswordHash("bcrypt-hash");
        user.setRole("BUYER");

        userDAO.create(user);

        Optional<User> result =
                userDAO.findByEmail(
                        "dao@test.com"
                );

        assertTrue(result.isPresent());

        assertEquals(
                "DAO Test User",
                result.get().getName()
        );

        assertEquals(
                "dao@test.com",
                result.get().getEmail()
        );

        assertEquals(
                "BUYER",
                result.get().getRole()
        );
    }

    @Test
    void findByEmailShouldReturnEmptyForUnknownUser()
            throws Exception {

        Optional<User> result =
                userDAO.findByEmail(
                        "missing@test.com"
                );

        assertTrue(result.isEmpty());
    }
}