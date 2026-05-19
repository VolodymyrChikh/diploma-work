package com.volodymyrchikh.abitandstudhelp.exception;

import lombok.Getter;

@Getter
public class PostNotFoundException extends RuntimeException {

    private final Long postId;

    public PostNotFoundException(String message, Long postId) {
        super(message);
        this.postId = postId;
    }
}
