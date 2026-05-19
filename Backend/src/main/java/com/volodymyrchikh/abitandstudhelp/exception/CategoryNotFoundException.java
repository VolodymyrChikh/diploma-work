package com.volodymyrchikh.abitandstudhelp.exception;

import lombok.Getter;

@Getter
public class CategoryNotFoundException extends RuntimeException {

    private final Long categoryId;

    public CategoryNotFoundException(String message, Long categoryId) {
        super(message);
        this.categoryId = categoryId;
    }
}
