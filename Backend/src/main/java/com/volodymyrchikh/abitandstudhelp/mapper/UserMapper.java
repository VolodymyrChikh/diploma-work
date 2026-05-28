package com.volodymyrchikh.abitandstudhelp.mapper;

import com.volodymyrchikh.abitandstudhelp.domain.AcademicGroup;
import com.volodymyrchikh.abitandstudhelp.domain.User;
import com.volodymyrchikh.abitandstudhelp.domain.UserStatus;
import com.volodymyrchikh.abitandstudhelp.domain.Specialty;
import com.volodymyrchikh.abitandstudhelp.dto.AcademicGroupResponse;
import com.volodymyrchikh.abitandstudhelp.dto.SpecialtyResponse;
import com.volodymyrchikh.abitandstudhelp.dto.UpdateUserRequest;
import com.volodymyrchikh.abitandstudhelp.dto.UserResponse;
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

        AcademicGroup academicGroup = user.getGroup();
        AcademicGroupResponse groupResponse = null;
        if (academicGroup != null) {
            groupResponse = new AcademicGroupResponse();
            groupResponse.setId(academicGroup.getId());
            groupResponse.setName(academicGroup.getName());
            groupResponse.setCreatedAt(academicGroup.getCreatedAt());
            groupResponse.setUpdatedAt(academicGroup.getUpdatedAt());
        }

        return UserResponse.builder()
                .id(user.getId())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .role(user.getRole())
                .specialtyResponse(specialtyResponse)
                .avatarLink(user.getAvatarLink())
                .groupResponse(groupResponse)
                .groupName(academicGroup != null ? academicGroup.getName() : null)
                .bio(user.getBio())
                .githubLink(user.getGithubLink())
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .build();
    }
}
