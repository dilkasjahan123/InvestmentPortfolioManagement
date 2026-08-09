package org.example.investmentfullproject.service;

import org.example.investmentfullproject.model.Role;
import org.example.investmentfullproject.model.User;
import org.example.investmentfullproject.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerUserSuccess() {

        User user = new User();
        user.setUsername("Megha");
        user.setPassword("123");
        user.setRole(Role.INVESTOR);

        when(userRepository.findByUsername("Megha"))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        User result = authService.registerUser(user);

        assertNotNull(result);
        assertEquals("Megha", result.getUsername());

        verify(userRepository, times(1))
                .save(any(User.class));
    }

    @Test
    void duplicateUsernameThrowsException() {

        User existingUser = new User();
        existingUser.setUsername("Megha");

        User newUser = new User();
        newUser.setUsername("Megha");

        when(userRepository.findByUsername("Megha"))
                .thenReturn(Optional.of(existingUser));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.registerUser(newUser));

        assertEquals(
                "Username already exists",
                exception.getMessage());
    }

    @Test
    void loginSuccess() {

        User user = new User();
        user.setUsername("Megha");
        user.setPassword("123");

        when(userRepository.findByUsername("Megha"))
                .thenReturn(Optional.of(user));

        User result =
                authService.login("Megha", "123");

        assertEquals(
                "Megha",
                result.getUsername());
    }

    @Test
    void invalidPasswordThrowsException() {

        User user = new User();
        user.setUsername("Megha");
        user.setPassword("123");

        when(userRepository.findByUsername("Megha"))
                .thenReturn(Optional.of(user));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.login(
                                "Megha",
                                "999"));

        assertEquals(
                "Invalid Password",
                exception.getMessage());
    }

    @Test
    void invalidUsernameThrowsException() {

        when(userRepository.findByUsername("Megha"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.login(
                                "Megha",
                                "123"));

        assertEquals(
                "Invalid Username",
                exception.getMessage());
    }
}