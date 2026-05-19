package com.volodymyrchikh.abitandstudhelp.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.volodymyrchikh.abitandstudhelp.common.AppConstants.EMAIL_REGEX;
import static com.volodymyrchikh.abitandstudhelp.common.AppConstants.PASSWORD_REGEX;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Прізвище не може бути пустим")
    private String lastName;

    @NotBlank(message = "Ім'я не може бути пустим")
    private String firstName;

    @NotBlank(message = "Електронна пошта не може бути пустою")
    @Email(regexp = EMAIL_REGEX, message = "Електронна пошта має бути валідною")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Пароль не може бути пустим")
    @Size(min = 8, message = "Пароль повинен бути не менше 8 символів")
    @Pattern(regexp = PASSWORD_REGEX,
            message = "Пароль має бути мінімум 8 символів, містити латинську велику літеру, " +
            "латинську малу літеру та хоча б одну цифру. Спецсимволи дозволені")
    private String password;

    @NotBlank(message = "Будь ласка, повторіть пароль")
    private String repeatedPassword;

    @NotNull(message = "Спеціальність не може бути пустою")
    private String specialty;

    private String avatarLink;

}
