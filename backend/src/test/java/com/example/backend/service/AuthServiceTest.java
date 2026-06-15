package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldReturnTokenWhenCredentialsAreValid() {
        String email = "test@test.com";
        String rawPassword = "password123";
        String expectedToken = "jwt.token.here";

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setPassword("encodedPassword");
        user.setRole("ROLE_STUDENT");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, user.getPassword())).thenReturn(true);
        when(jwtUtils.generateToken(email, List.of("ROLE_STUDENT"), user.getId())).thenReturn(expectedToken);

        String actualToken = authService.login(email, rawPassword);

        assertEquals(expectedToken, actualToken);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        String email = "notfound@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(email, "anyPassword");
        });
        assertEquals("Błędny dane", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        String email = "test@test.com";
        String rawPassword = "wrongPassword";

        User user = new User();
        user.setEmail(email);
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, user.getPassword())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(email, rawPassword);
        });
        assertEquals("Błędne dane ", exception.getMessage());
    }
}