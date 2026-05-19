package com.volodymyrchikh.abitandstudhelp.mapper;

import com.volodymyrchikh.abitandstudhelp.domain.User;
import com.volodymyrchikh.abitandstudhelp.domain.UserStatus;
import com.volodymyrchikh.abitandstudhelp.domain.StudentGroup;
import com.volodymyrchikh.abitandstudhelp.domain.Specialty;
import com.volodymyrchikh.abitandstudhelp.dto.SpecialtyResponse;
import com.volodymyrchikh.abitandstudhelp.dto.UpdateUserRequest;
import com.volodymyrchikh.abitandstudhelp.dto.UserResponse;
import com.volodymyrchikh.abitandstudhelp.exception.GroupNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User updateUser(User toUpdateUser, UpdateUserRequest request) {
        if (request.getFirstName() != null) {
            toUpdateUser.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            toUpdateUser.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            toUpdateUser.setEmail(request.getEmail());
        }
        if (request.getAvatarLink() != null) {
            toUpdateUser.setAvatarLink(request.getAvatarLink());
        }
        if (request.getGroupName() != null) {
            try {
                toUpdateUser.setGroupName(StudentGroup.valueOf(request.getGroupName().replace("-", "_").toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new GroupNotFoundException("Групу " + request.getGroupName() + " не знайдено", request.getGroupName());
            }
        }
        if (request.getBio() != null) {
            toUpdateUser.setBio(request.getBio());
        }
        if (request.getGithubLink() != null) {
            toUpdateUser.setGithubLink(request.getGithubLink());
        }
        if (request.getStatus() != null) {
            try {
                toUpdateUser.setStatus(UserStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Ignore or handle invalid status
            }
        }
        return toUpdateUser;
    }

    public UserResponse mapToResponse(User user) {
        Specialty specialty = user.getSpecialty();
        SpecialtyResponse specialtyResponse = null;
        if (specialty != null) {
            specialtyResponse = new SpecialtyResponse(
                    specialty.getId(),
                    specialty.getName(),
                    specialty.getNumber(),
                    specialty.getAbout(),
                    specialty.getCreatedAt(),
                    specialty.getUpdatedAt()
            );
        }
        return UserResponse.builder()
                .id(user.getId())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .email(user.getEmail())
            .role(user.getRole())
                .specialtyResponse(specialtyResponse)
                .avatarLink(user.getAvatarLink())
                .groupName(user.getGroupName() != null ? user.getGroupName().name().replace("_", "-") : null)
                .bio(user.getBio())
                .githubLink(user.getGithubLink())
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .build();
    }
}
