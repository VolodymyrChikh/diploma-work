package com.volodymyrchikh.abitandstudhelp.exception;

public class AcademicGroupNotFoundException extends RuntimeException {
    public AcademicGroupNotFoundException(String message) {
        super(message);
    }

    public AcademicGroupNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

