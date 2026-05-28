package com.volodymyrchikh.abitandstudhelp.dto;

import lombok.Builder;

@Builder
public record UserResponse(Long id,
                           String lastName,
                           String firstName,
                           String email,
                           String role,
                           SpecialtyResponse specialtyResponse,
                           String avatarLink,
                           AcademicGroupResponse groupResponse,
                           String groupName,
                           String bio,
                           String githubLink,
                           String status
) {
}
