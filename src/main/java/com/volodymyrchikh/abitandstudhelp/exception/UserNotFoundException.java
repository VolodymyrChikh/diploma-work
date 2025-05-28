package com.volodymyrchikh.abitandstudhelp.exception;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {

    private final Long userId;

    public UserNotFoundException(String message, Long userId) {
        super(message);
        this.userId = userId;
    }

    public UserNotFoundException(String formatted, @NotBlank String email) {
        super(formatted);
        this.userId = null;
    }
}
