package com.volodymyrchikh.abitandstudhelp.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@RequiredArgsConstructor
@Getter
public class FieldAlreadyUsedException extends RuntimeException{

    private final String fieldName;
    private final String message;

}
