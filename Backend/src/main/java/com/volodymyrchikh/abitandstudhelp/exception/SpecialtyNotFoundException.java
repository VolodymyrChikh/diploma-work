package com.volodymyrchikh.abitandstudhelp.exception;

import lombok.Getter;

@Getter
public class SpecialtyNotFoundException extends RuntimeException {

    private final Long specialtyId;
    private final String specialtyName;

    public SpecialtyNotFoundException(String message, Long specialtyId, String specialtyName) {
        super(message);
        this.specialtyId = specialtyId;
        this.specialtyName = specialtyName;
    }

}
