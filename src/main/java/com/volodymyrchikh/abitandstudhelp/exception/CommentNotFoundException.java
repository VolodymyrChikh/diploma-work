package com.volodymyrchikh.abitandstudhelp.exception;

import lombok.Getter;

@Getter
public class CommentNotFoundException extends RuntimeException {

    private final Long commentId;

    public CommentNotFoundException(String message, Long commentId) {
        super(message);
        this.commentId = commentId;
    }
}
