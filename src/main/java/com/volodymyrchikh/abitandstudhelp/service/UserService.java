package com.volodymyrchikh.abitandstudhelp.service;

import com.querydsl.core.types.Predicate;
import com.volodymyrchikh.abitandstudhelp.common.Role;
import com.volodymyrchikh.abitandstudhelp.domain.Specialty;
import com.volodymyrchikh.abitandstudhelp.domain.User;
import com.volodymyrchikh.abitandstudhelp.dto.RegisterRequest;
import com.volodymyrchikh.abitandstudhelp.dto.UpdateUserRequest;
import com.volodymyrchikh.abitandstudhelp.dto.UserResponse;
import com.volodymyrchikh.abitandstudhelp.exception.SpecialtyNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.UserNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.WrongCredentialsException;
import com.volodymyrchikh.abitandstudhelp.mapper.UserMapper;
import com.volodymyrchikh.abitandstudhelp.repository.SpecialtyRepository;
import com.volodymyrchikh.abitandstudhelp.repository.UserRepository;
import com.volodymyrchikh.abitandstudhelp.security.UsersDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SpecialtyRepository specialtyRepository;
    private final PasswordEncoder passwordEncoder;

    public User save(RegisterRequest registerRequest) {
        Specialty specialty = specialtyRepository.findByName(registerRequest.getSpecialty())
                .orElseThrow(() -> new SpecialtyNotFoundException("Specialty of id=[%s] or name=[%s] not found"
                        .formatted(null, registerRequest.getSpecialty()), null, registerRequest.getSpecialty()));

        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.ROLE_USER.name())
                .specialty(specialty)
                .build();

        return userRepository.save(user);
    }

    public UserResponse getById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::mapToResponse)
                .orElseThrow(() -> new UserNotFoundException(("User with id=[%s] not found").formatted(userId), userId));
    }

    public Page<UserResponse> getAll(Pageable pageable, Predicate filters) {
        return userRepository.findAll(filters, pageable).map(userMapper::mapToResponse);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User toUpdateUser = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User with id=[%s] not found".formatted(id), id));

        if (userRepository.existsByEmail((request.getEmail()))) {
            throw new WrongCredentialsException("Email is already taken!", request);
        }

        User newUser = userRepository.save(userMapper.updateUser(toUpdateUser, request));
        return userMapper.mapToResponse(newUser);
    }

    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserResponse getAuthenticatedUser() {
        UsersDetails userDetails = (UsersDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        User user = findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email=[%s] not found"
                        .formatted(email), email));
        return userMapper.mapToResponse(user);

    }
}
