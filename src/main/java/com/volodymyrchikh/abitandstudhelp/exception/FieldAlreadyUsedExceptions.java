package com.volodymyrchikh.abitandstudhelp.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class FieldAlreadyUsedExceptions extends RuntimeException {

    private final List<FieldAlreadyUsedException> exceptions;

}
