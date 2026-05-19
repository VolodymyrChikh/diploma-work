package com.volodymyrchikh.abitandstudhelp.exception;

import com.volodymyrchikh.abitandstudhelp.dto.UpdateUserRequest;
import lombok.Getter;

@Getter
public class WrongCredentialsException extends RuntimeException {

    public static final String DEFAULT_DETAIL = "Невірний email або пароль";

    private final String title;
    private final Object credentials;

    public WrongCredentialsException(String title, UpdateUserRequest credentials) {
        super(title);
        this.title = title;
        this.credentials = credentials;
    }

    public WrongCredentialsException(String title, String detail) {
        super(title);
        this.title = title;
        this.credentials = detail;
    }
}
