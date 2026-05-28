package com.volodymyrchikh.abitandstudhelp.service;

import com.querydsl.core.types.Predicate;
import com.volodymyrchikh.abitandstudhelp.common.Role;
import com.volodymyrchikh.abitandstudhelp.domain.AcademicGroup;
import com.volodymyrchikh.abitandstudhelp.domain.Specialty;
import com.volodymyrchikh.abitandstudhelp.domain.User;
import com.volodymyrchikh.abitandstudhelp.dto.RegisterRequest;
import com.volodymyrchikh.abitandstudhelp.dto.StorageUploadResponse;
import com.volodymyrchikh.abitandstudhelp.dto.UpdateUserRequest;
import com.volodymyrchikh.abitandstudhelp.dto.UserResponse;
import com.volodymyrchikh.abitandstudhelp.common.ResourceType;
import com.volodymyrchikh.abitandstudhelp.exception.SpecialtyNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.GroupNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.UserNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.WrongCredentialsException;
import com.volodymyrchikh.abitandstudhelp.mapper.UserMapper;
import com.volodymyrchikh.abitandstudhelp.repository.AcademicGroupRepository;
import com.volodymyrchikh.abitandstudhelp.repository.SpecialtyRepository;
import com.volodymyrchikh.abitandstudhelp.repository.UserRepository;
import com.volodymyrchikh.abitandstudhelp.security.UsersDetails;
import com.volodymyrchikh.abitandstudhelp.service.storage.ObjectStorageService;
import com.volodymyrchikh.abitandstudhelp.service.storage.UploadFileValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SpecialtyRepository specialtyRepository;
    private final AcademicGroupRepository academicGroupRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectStorageService objectStorageService;
    private final UploadFileValidator uploadFileValidator;

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

    public User saveOAuthUser(String email, String firstName, String lastName, String avatarUrl) {
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .avatarLink(avatarUrl)
                .role(Role.ROLE_USER.name())
                .password(passwordEncoder.encode(java.util.UUID.randomUUID().toString()))
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

        if (!toUpdateUser.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new WrongCredentialsException("Email is already taken!", request);
        }

        if (request.getSpecialtyId() != null) {
            Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                    .orElseThrow(() -> new SpecialtyNotFoundException("Specialty of id=[%s] not found"
                            .formatted(request.getSpecialtyId()), request.getSpecialtyId(), null));
            toUpdateUser.setSpecialty(specialty);
        }

        if (request.getGroupId() != null) {
            AcademicGroup academicGroup = academicGroupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new GroupNotFoundException("Групу з id " + request.getGroupId() + " не знайдено", String.valueOf(request.getGroupId())));
            toUpdateUser.setGroup(academicGroup);
        } else {
            toUpdateUser.setGroup(null);
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
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found", email));
        return userMapper.mapToResponse(user);

    }

    @Transactional
    public UserResponse uploadUserAvatar(Long userId, MultipartFile avatarFile) throws java.io.IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(("User with id=[%s] not found").formatted(userId), userId));

        uploadFileValidator.validateFiles(ResourceType.IMAGE, java.util.List.of(avatarFile));

        StorageUploadResponse uploadedAvatar = objectStorageService.uploadUserAvatar(avatarFile, user.getEmail());
        String previousAvatarLink = user.getAvatarLink();

        user.setAvatarLink(uploadedAvatar.getUrl());
        User savedUser = userRepository.save(user);

        if (previousAvatarLink != null && !previousAvatarLink.equals(uploadedAvatar.getUrl())) {
            objectStorageService.deleteFileByUrl(previousAvatarLink);
        }

        return userMapper.mapToResponse(savedUser);
    }

    @Transactional
    public UserResponse deleteUserAvatar(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(("User with id=[%s] not found").formatted(userId), userId));

        String previousAvatarLink = user.getAvatarLink();
        user.setAvatarLink(null);
        User savedUser = userRepository.save(user);

        objectStorageService.deleteFileByUrl(previousAvatarLink);

        return userMapper.mapToResponse(savedUser);
    }
}
