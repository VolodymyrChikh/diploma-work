package com.volodymyrchikh.abitandstudhelp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import static com.volodymyrchikh.abitandstudhelp.common.AppConstants.EMAIL_REGEX;

@Data
@Builder
public class UpdateUserRequest {

    @Pattern(regexp = "[A-Z][a-z]+",
            message = "Прізвише має починатися з великої літери")
    private String lastName;

    @Pattern(regexp = "[A-Z][a-z]+",
            message = "Ім'я має починатися з великої літери")
    private String firstName;

    @Email(regexp = EMAIL_REGEX, message = "Електронна пошта має бути валідною")
    private String email;

    private Long specialtyId;

    private String avatarLink;

}
