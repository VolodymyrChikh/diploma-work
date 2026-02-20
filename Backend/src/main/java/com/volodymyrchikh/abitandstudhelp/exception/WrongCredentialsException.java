package com.volodymyrchikh.abitandstudhelp.exception;

import com.volodymyrchikh.abitandstudhelp.dto.UpdateUserRequest;
import lombok.Getter;

@Getter
public class WrongCredentialsException extends RuntimeException {

    private final String title;
    private final Object credentials;

    public WrongCredentialsException(String title, UpdateUserRequest credentials) {
        this.title = title;
        this.credentials = credentials;
    }

    public WrongCredentialsException(String incorrectPassword, String s) {
        this.title = incorrectPassword;
        this.credentials = s;
    }
}
