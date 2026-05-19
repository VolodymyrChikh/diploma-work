package com.volodymyrchikh.abitandstudhelp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    @NotBlank(message = "Електронна пошта не може бути пустою")
    private String email;

    @NotBlank(message = "Пароль не може бути пустим")
    private String password;

}
