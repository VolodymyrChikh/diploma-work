package com.volodymyrchikh.abitandstudhelp.service;

import com.volodymyrchikh.abitandstudhelp.domain.User;
import com.volodymyrchikh.abitandstudhelp.dto.AuthenticationRequest;
import com.volodymyrchikh.abitandstudhelp.exception.WrongCredentialsException;
import com.volodymyrchikh.abitandstudhelp.mapper.UserMapper;
import com.volodymyrchikh.abitandstudhelp.security.auth.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AuthenticationServiceTest {

    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final UserMapper userMapper = mock(UserMapper.class);
    private final UserService userService = mock(UserService.class);
    private final AuthenticationService authenticationService = new AuthenticationService(
            passwordEncoder,
            jwtService,
            userMapper,
            userService
    );

    @Test
    void authenticateReturnsGenericErrorWhenEmailDoesNotExist() {
        String email = "student@example.com";
        when(userService.findUserByEmail(email)).thenReturn(Optional.empty());

        WrongCredentialsException exception = assertThrows(WrongCredentialsException.class, () ->
                authenticationService.authenticate(new AuthenticationRequest(email, "Password1")));

        assertEquals(WrongCredentialsException.DEFAULT_DETAIL, exception.getCredentials());
        assertFalse(exception.getMessage().contains(email));
        assertFalse(String.valueOf(exception.getCredentials()).contains(email));
        verifyNoInteractions(passwordEncoder, jwtService, userMapper);
    }

    @Test
    void authenticateReturnsGenericErrorWhenPasswordIsWrong() {
        User user = User.builder()
                .email("student@example.com")
                .password("encoded-password")
                .build();
        when(userService.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword1", user.getPassword())).thenReturn(false);

        WrongCredentialsException exception = assertThrows(WrongCredentialsException.class, () ->
                authenticationService.authenticate(new AuthenticationRequest(user.getEmail(), "WrongPassword1")));

        assertEquals(WrongCredentialsException.DEFAULT_DETAIL, exception.getCredentials());
        verifyNoInteractions(jwtService, userMapper);
    }
}
