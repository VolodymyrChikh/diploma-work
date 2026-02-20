package com.volodymyrchikh.abitandstudhelp.mapper;

import com.volodymyrchikh.abitandstudhelp.domain.User;
import com.volodymyrchikh.abitandstudhelp.domain.Specialty;
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
                .password(user.getPassword())
                .specialtyResponse(specialtyResponse)
                .avatarLink(user.getAvatarLink())
                .build();
    }
}