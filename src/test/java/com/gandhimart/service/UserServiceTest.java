package com.gandhimart.service;

import com.gandhimart.dao.UserDAO;
import com.gandhimart.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserDAO userDAO;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDAO = mock(UserDAO.class);
        userService = new UserService(userDAO);
    }

    @Test
    void registerShouldRejectShortPassword() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.register(
                                "Test User",
                                "test@example.com",
                                "123",
                                "BUYER"
                        )
                );

        assertEquals(
                "Password must be at least 6 characters",
                exception.getMessage()
        );

        verifyNoInteractions(userDAO);
    }

    @Test
    void registerShouldRejectDuplicateEmail()
            throws Exception {

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail(
                "test@example.com"
        );

        when(userDAO.findByEmail(
                "test@example.com"
        )).thenReturn(
                java.util.Optional.of(existingUser)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.register(
                                "Test User",
                                "test@example.com",
                                "password123",
                                "BUYER"
                        )
                );

        assertEquals(
                "Email already registered",
                exception.getMessage()
        );

        verify(
                userDAO
        ).findByEmail(
                "test@example.com"
        );

        verify(
                userDAO,
                never()
        ).create(any(User.class));
    }
}