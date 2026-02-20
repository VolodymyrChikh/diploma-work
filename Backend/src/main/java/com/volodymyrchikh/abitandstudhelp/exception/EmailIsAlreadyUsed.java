package com.volodymyrchikh.abitandstudhelp.exception;

import lombok.Getter;

@Getter
public class EmailIsAlreadyUsed extends RuntimeException {

    private final String email;

    public EmailIsAlreadyUsed(String message, String email) {
        super(message);
        this.email = email;
    }
}
