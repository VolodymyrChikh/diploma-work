package com.volodymyrchikh.abitandstudhelp.dto;

import lombok.Builder;

@Builder
public record UserResponse(Long id,
                           String lastName,
                           String firstName,
                           String email,
                           String password,
                           SpecialtyResponse specialtyResponse,
                           String avatarLink
) {
}
