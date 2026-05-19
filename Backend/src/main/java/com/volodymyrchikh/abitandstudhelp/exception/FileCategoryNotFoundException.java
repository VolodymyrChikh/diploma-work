package com.volodymyrchikh.abitandstudhelp.exception;

public class FileCategoryNotFoundException extends RuntimeException {
    public FileCategoryNotFoundException(String message) {
        super(message);
    }

    public FileCategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

