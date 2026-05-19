package com.volodymyrchikh.abitandstudhelp.service;

import com.volodymyrchikh.abitandstudhelp.domain.User;
import com.volodymyrchikh.abitandstudhelp.dto.AuthenticationRequest;
import com.volodymyrchikh.abitandstudhelp.dto.AuthenticationResponse;
import com.volodymyrchikh.abitandstudhelp.dto.RegisterRequest;
import com.volodymyrchikh.abitandstudhelp.dto.TokenResponse;
import com.volodymyrchikh.abitandstudhelp.exception.*;
import com.volodymyrchikh.abitandstudhelp.mapper.UserMapper;
import com.volodymyrchikh.abitandstudhelp.security.UsersDetails;
import com.volodymyrchikh.abitandstudhelp.security.auth.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final UserService userService;

    @Transactional
    public AuthenticationResponse register(RegisterRequest registerRequest) {
        userService.findUserByEmail(registerRequest.getEmail())
                .ifPresent(user -> {
                    throw new EmailIsAlreadyUsed("Email is already used", registerRequest.getEmail());
                });
        User savedUser = userService.save(registerRequest);

        TokenResponse tokenResponse = getTokenResponse(savedUser);
        return new AuthenticationResponse(userMapper.mapToResponse(savedUser), tokenResponse);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userService.findUserByEmail(request.getEmail()).orElseThrow(() ->
                new WrongCredentialsException("Невірні дані для входу", WrongCredentialsException.DEFAULT_DETAIL));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new WrongCredentialsException("Невірні дані для входу", WrongCredentialsException.DEFAULT_DETAIL);
        }

        TokenResponse tokenResponse = getTokenResponse(user);
        return new AuthenticationResponse(userMapper.mapToResponse(user), tokenResponse);
    }

    @Transactional
    public AuthenticationResponse authenticateOAuthUser(String email, String firstName, String lastName, String avatarUrl) {
        User user = userService.findUserByEmail(email).orElse(null);

        if (user == null) {
            user = userService.saveOAuthUser(email, firstName, lastName, avatarUrl);
        }

        TokenResponse tokenResponse = getTokenResponse(user);
        return new AuthenticationResponse(userMapper.mapToResponse(user), tokenResponse);
    }

    private TokenResponse getTokenResponse(User user) {
        UsersDetails userDetails = new UsersDetails(user);
        var jwtToken = jwtService.generateToken(userDetails);
        long currentTime = System.currentTimeMillis();
        long expirationTime = currentTime + (1000 * 60 * 60 * 24);

        String refreshToken = getRefreshToken(user);
        return new TokenResponse(jwtToken, currentTime, expirationTime, refreshToken);
    }

    private String getRefreshToken(User user) {
        UsersDetails userDetails = new UsersDetails(user);
        return jwtService.generateRefreshToken(userDetails);
    }

}
