package com.volodymyrchikh.abitandstudhelp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import lombok.Builder;
import lombok.Data;

import static com.volodymyrchikh.abitandstudhelp.common.AppConstants.EMAIL_REGEX;

@Data
@Builder
public class UpdateUserRequest {

    @NotBlank(message = "Прізвище не може бути пустим")
    private String lastName;

    @NotBlank(message = "Ім'я не може бути пустим")
    private String firstName;

    @Email(regexp = EMAIL_REGEX, message = "Електронна пошта має бути валідною")
    private String email;

    private Long specialtyId;

    private String avatarLink;

    private Long groupId;

    @Size(max = 500, message = "Розмір опису про себе не може перевищувати 500 символів")
    private String bio;

    @URL(message = "Посилання має бути валідним URL")
    private String githubLink;

    private String status;

}
